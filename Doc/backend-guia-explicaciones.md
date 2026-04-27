# Guía de explicaciones — Backend (APILoginManager + APIRest)

Este documento recoge las explicaciones conceptuales dadas durante el desarrollo del backend del proyecto de reservas de servicios para eventos.

---

## Paso 0 — Corrección en APILoginManager: JWT y validación de contraseña

### ¿Qué es un JWT y por qué el subject importa?

Un **JWT (JSON Web Token)** tiene tres partes:
1. **Header**: algoritmo de firma (aquí HS384)
2. **Payload**: los "claims" — datos que viajan en el token: el `subject` (quién es el usuario), roles, y fecha de expiración
3. **Signature**: garantiza que el token no fue alterado

El **subject** es el identificador principal del token. Cuando el APIRest lo recibe, extrae el subject para saber quién hace la petición. El problema era que el subject era el **email**, pero la base de datos usa **`username`** (aliasUsuario) como clave primaria y como FK en `reservas`. Si el APIRest usara el email del token para guardar una reserva, insertaría un email donde la BD espera un username → error de FK.

**Corrección:** pasar `usuario.getAliasUsuario()` en lugar de `usuario.getEmail()` al método `generateToken()`.

### ¿Por qué usar `passwordEncoder.matches()` y no comparar strings?

BCrypt es un algoritmo de hash **unidireccional**: dado un texto plano genera un hash, pero no puedes hacer el proceso inverso. Además, añade una "sal" aleatoria para que dos contraseñas iguales generen hashes distintos.

```
"1234" → "$2a$10$K1lVoZWyxN..." (hash único con sal)
"1234" → "$2a$10$mP3dQ8nZ5k..." (diferente cada vez)
```

Por eso **nunca** se comparan strings. `matches(rawPassword, hashedPassword)` aplica el mismo algoritmo al texto plano con la sal embebida en el hash y comprueba si coincide.

**El bug:** el endpoint `/login` no validaba la contraseña — cualquier persona con el email de un usuario podía obtener un JWT válido.

---

## Paso 1 — Configurar APIRest: `application.properties` y Scheduling

### `ddl-auto`: los modos de generación de esquema

Spring Boot + Hibernate pueden gestionar el esquema de la BD automáticamente. El valor de `spring.jpa.hibernate.ddl-auto` controla cómo:

| Valor | Comportamiento |
|---|---|
| `create` | **Borra y recrea** todas las tablas en cada arranque. Útil solo para el primer setup. Peligroso con datos reales. |
| `create-drop` | Igual que `create` pero también borra las tablas al parar la app. Para tests. |
| `update` | Compara las entidades con el esquema real y **solo añade** lo que falta (columnas, tablas). No borra datos. Para desarrollo activo. |
| `validate` | Solo comprueba que el esquema coincide con las entidades. Falla si hay diferencias. Para producción. |
| `none` | No hace nada. El esquema se gestiona manualmente. |

> **Importante:** El APILoginManager usa `create`. Eso significa que cada vez que arranca **destruye todos los datos**. Para desarrollo, cambiar a `update` en cuanto la BD tenga datos reales.

### `@EnableScheduling` — tareas programadas en Spring

Sin esta anotación, los métodos marcados con `@Scheduled` son ignorados completamente. Spring no activa el mecanismo de scheduling por defecto para evitar crear hilos innecesarios.

Se puede poner en la clase principal (`@SpringBootApplication`) o en cualquier clase `@Configuration`. En un proyecto pequeño, la clase main es lo más limpio.

---

## Paso 2 — Entidades JPA: conceptos clave

### ¿Qué es JPA y por qué se usa?

**JPA (Java Persistence API)** es una especificación estándar de Java para mapear objetos Java a tablas de base de datos. **Hibernate** es la implementación que usa Spring Boot por defecto. El resultado: en lugar de escribir SQL manualmente, defines clases Java y JPA genera las queries.

### Anotaciones de entidad explicadas

```java
@Entity          // Esta clase es una entidad JPA → tiene tabla en BD
@Table(name = "tipos")  // Nombre exacto de la tabla (sin esto usa el nombre de clase)
public class Tipo implements Serializable {
```

**`@Entity`**: Marca la clase como entidad persistible. JPA la registra en su contexto y puede hacer operaciones CRUD con ella.

**`@Table(name = "...")`**: Especifica el nombre de la tabla en BD. Sin esto, JPA usa el nombre de la clase en minúsculas. Importante cuando el nombre de la tabla no coincide exactamente con el nombre de la clase.

**`implements Serializable`**: JPA puede necesitar serializar entidades para cachés de segundo nivel o transferencias entre JVMs. Es buena práctica aunque no siempre obligatorio.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id_tipo")
private Long idTipo;
```

**`@Id`**: Marca el campo como clave primaria.

**`@GeneratedValue(strategy = GenerationType.IDENTITY)`**: Delega el auto-incremento a la BD (`AUTO_INCREMENT` en MySQL). Otras estrategias:
- `SEQUENCE`: usa secuencias de BD (Oracle, PostgreSQL)
- `AUTO`: JPA elige la estrategia según el dialecto
- Sin `@GeneratedValue`: la aplicación asigna el ID manualmente

**`@Column(nullable = false, length = 45)`**: Configura la columna en BD. `nullable = false` genera `NOT NULL`. `length = 45` genera `VARCHAR(45)`. Sin `@Column`, JPA usa el nombre del campo y valores por defecto.

### Anotaciones de Lombok

**`@Data`**: Atajo que genera `@Getter`, `@Setter`, `@ToString`, `@EqualsAndHashCode` y `@RequiredArgsConstructor`. Cuidado: puede causar problemas con relaciones bidireccionales en `@ToString` y `@EqualsAndHashCode` (bucles infinitos).

**`@Getter` + `@Setter`** por separado: Más explícito y seguro cuando hay relaciones JPA. Evita el `@ToString`/`@EqualsAndHashCode` automático.

**`@NoArgsConstructor`**: Constructor sin parámetros. **JPA lo requiere obligatoriamente** para poder instanciar entidades al leer de BD (usa reflection).

**`@AllArgsConstructor`**: Constructor con todos los campos. Útil para crear objetos con todos los datos en una línea.

**`@Builder`**: Genera el patrón Builder. En vez de `new Tipo(null, "DJ", "Músico")`, puedes escribir `Tipo.builder().nombre("DJ").descripcion("Músico").build()`. Más legible cuando hay muchos campos.

---

## Paso 3 — Ajuste de entidades `Usuario` y `UsuarioPerfiles`

### El problema del esquema inconsistente

Las dos APIs comparten la **misma base de datos**. Si cada una define la tabla `usuario` de forma diferente, Hibernate fallará al intentar sincronizar los esquemas:

- APILoginManager tenía: PK = `id_usuario` (Long auto-increment) + columna `alias_usuario` (String)
- Spec del enunciado: PK = `username` (VARCHAR(45))
- APIRest (ya preparado): campo `username` como `@Id` String

**Solución:** alinear el APILoginManager con la spec. El campo Java se mantiene como `aliasUsuario` (para evitar conflicto con `UserDetails.getUsername()` que Lombok también generaría), pero la **columna SQL se mapea a `username`** mediante `@Column(name = "username")`.

### ¿Por qué `aliasUsuario` y no `username` como nombre del campo Java?

La interfaz `UserDetails` de Spring Security exige implementar `getUsername()`. Si el campo Java se llamara `username`, Lombok generaría `getUsername()` como getter y tendríamos dos métodos `getUsername()` → **error de compilación**.

Al llamar al campo `aliasUsuario`, Lombok genera `getAliasUsuario()`, y el método `getUsername()` de `UserDetails` se implementa manualmente devolviendo `this.aliasUsuario`.

```java
@Id
@Column(name = "username", ...)  // columna en BD: username ✓
private String aliasUsuario;     // nombre Java: aliasUsuario (sin conflicto con Lombok)

@Override
public String getUsername() {
    return aliasUsuario;  // devuelve el alias como identificador Spring Security
}
```

### `@EmbeddedId` y claves compuestas

Cuando una tabla tiene **clave primaria compuesta** (varios campos forman juntos la PK), JPA necesita una clase auxiliar que represente esa clave:

```java
@Embeddable  // Esta clase puede incrustarse en otra
public class UsuarioPerfilesId implements Serializable {
    @Column(name = "username")
    private String aliasUsuario;   // FK → usuario.username

    @Column(name = "id_perfil")
    private Long idPerfil;         // FK → perfiles.id_perfil
}
```

En la entidad que usa esa clave compuesta:

```java
@EmbeddedId  // La PK está "incrustada" en este objeto
private UsuarioPerfilesId idUsuarioPerfiles;
```

### `@MapsId` — sincronizar FK con PK compuesta

`@MapsId("nombreCampo")` le dice a JPA: "esta relación `@ManyToOne` es parte de la PK compuesta, y su valor corresponde al campo `nombreCampo` del `@EmbeddedId`".

> **Importante:** el string en `@MapsId` debe ser el **nombre del campo Java** en la clase `@Embeddable`, no el nombre de la columna SQL.

```java
@MapsId("aliasUsuario")            // campo en UsuarioPerfilesId → aliasUsuario ✓
@ManyToOne(optional = false)
@JoinColumn(name = "username")     // columna FK en la tabla usuario_perfiles
private Usuario usuario;

@MapsId("idPerfil")                // campo en UsuarioPerfilesId → idPerfil ✓
@ManyToOne(optional = false)
@JoinColumn(name = "id_perfil")
private Perfil perfil;
```

Sin `@MapsId`, JPA no sabe que la FK `username` y el campo `aliasUsuario` del `@EmbeddedId` son el mismo valor, lo que puede generar errores de inserción o queries incorrectas.

### `apellidos` en lugar de `primerApellido` + `segundoApellido`

El esquema del enunciado define `APELLIDOS VARCHAR(45)` como campo único. El modelo del frontend también usa `apellidos: string`. Mantener dos campos separados en la BD cuando la spec pide uno solo crea una divergencia innecesaria.

---

## Paso 4 — Entidades `Evento` y `Reserva`

### ENUMs en JPA: `@Enumerated(EnumType.STRING)` vs `EnumType.ORDINAL`

Cuando un campo en BD tiene un conjunto fijo de valores (como el estado de un evento), en Java se modela con un `enum`. JPA puede persistirlo de dos formas:

| Estrategia | ¿Qué guarda en BD? | Ejemplo |
|---|---|---|
| `EnumType.ORDINAL` | El **índice numérico** del enum (0, 1, 2...) | `ACTIVO=0`, `CANCELADO=1` |
| `EnumType.STRING` | El **nombre** del valor enum como texto | `"ACTIVO"`, `"CANCELADO"` |

**¿Por qué usar siempre `STRING`?**

Con `ORDINAL`, si algún día reordenas o añades un valor al enum, todos los registros existentes en BD apuntan a valores incorrectos. Por ejemplo, si insertas `PAUSADO` entre `ACTIVO` y `CANCELADO`, ahora `CANCELADO=2` en lugar de `1` → los datos ya guardados quedan corruptos.

Con `STRING` esto no ocurre: el texto `"CANCELADO"` siempre mapea a `CANCELADO`, independientemente del orden del enum.

```java
public enum Estado { ACTIVO, CANCELADO, TERMINADO }

@Enumerated(EnumType.STRING)
@Column(nullable = false, length = 10)
private Estado estado;
// → guarda "ACTIVO", "CANCELADO" o "TERMINADO" en BD
```

Los ENUMs se definen como clases internas de la entidad (`public enum Estado { ... }` dentro de `Evento`). Es una buena práctica porque el enum pertenece semánticamente a esa entidad. Si varios tipos tuvieran el mismo enum, se movería a su propio archivo.

### `BigDecimal` vs `Double` para dinero

Nunca usar `double` o `float` para dinero. Los números en coma flotante representan fracciones binarias, y hay valores decimales que no tienen representación exacta en binario:

```
0.1 + 0.2 = 0.30000000000000004  (en double)
0.1 + 0.2 = 0.3                  (en BigDecimal)
```

En una app de reservas, un error de cálculo en precios es inaceptable. `BigDecimal` representa decimales con **precisión arbitraria exacta**.

En JPA, `precision` es el número total de dígitos y `scale` son los decimales:

```java
@Column(nullable = false, precision = 8, scale = 2)
private BigDecimal precio;
// → DECIMAL(8,2) en MySQL: hasta 999999.99
```

### `@ManyToOne` — relación muchos a uno

`Evento` tiene un tipo (`Tipo`). Muchos eventos pueden tener el mismo tipo → relación **muchos a uno** desde `Evento` hacia `Tipo`.

```java
@ManyToOne(optional = false)
@JoinColumn(name = "id_tipo", nullable = false)
private Tipo tipo;
```

- **`@ManyToOne`**: "este objeto pertenece a otro". JPA añade la columna FK en la tabla de esta entidad.
- **`optional = false`**: equivale a `NOT NULL` en el nivel JPA (Hibernate puede usarlo para optimizar queries).
- **`@JoinColumn(name = "id_tipo")`**: nombre de la columna FK en la tabla `eventos`. Sin esto, JPA genera un nombre automático (`tipo_id_tipo`).

No se añade `@OneToMany` en `Tipo` hacia `Evento` porque no se necesita navegar desde un tipo a todos sus eventos en ningún caso de uso. Añadir relaciones bidireccionales sin uso real solo complica el código y puede causar problemas de rendimiento (carga lazy/eager no esperada).

### `Reserva.username` como String en lugar de `@ManyToOne` a `Usuario`

La relación entre `Reserva` y `Usuario` existe en BD (hay FK), pero en el código se modela como un `String username` simple en lugar de `@ManyToOne Usuario usuario`.

**¿Por qué?**

1. **Las dos APIs son independientes.** El `Usuario` completo (con roles, password, etc.) lo gestiona el APILoginManager. El APIRest no necesita cargar el objeto `Usuario` entero para trabajar con una reserva — solo necesita saber a quién pertenece.

2. **Evitar dependencias innecesarias.** Si `Reserva` tuviera `@ManyToOne Usuario`, cada vez que JPA cargara una reserva intentaría hacer un JOIN con `usuario`. Esa información no aporta nada al caso de uso del APIRest.

3. **El username viene del JWT.** El APIRest extrae el `username` del token JWT (que el APILoginManager generó). Lo recibe como String y lo guarda directamente. No necesita el objeto completo.

```java
// Simple y directo — el username llega del JWT
@Column(nullable = false, length = 45)
private String username;

// En lugar de:
// @ManyToOne → cargaría el objeto Usuario entero innecesariamente
```

---

## Paso 5 — Repositorios: Spring Data JPA

### ¿Qué es `JpaRepository` y por qué no escribimos SQL?

`JpaRepository<Entidad, TipoPK>` es una interfaz de Spring Data JPA. Solo con extenderla, Spring genera automáticamente en tiempo de arranque una implementación completa con estos métodos listos para usar:

```java
findAll()           // SELECT * FROM tabla
findById(id)        // SELECT * WHERE pk = id  → devuelve Optional<T>
save(entidad)       // INSERT o UPDATE (si ya tiene ID)
deleteById(id)      // DELETE WHERE pk = id
count()             // SELECT COUNT(*)
existsById(id)      // SELECT COUNT(*) > 0
```

No hay que escribir una sola línea de SQL para estas operaciones básicas.

### Métodos derivados — Spring genera la query del nombre del método

La característica más potente de Spring Data JPA: si el nombre del método sigue una convención, Spring genera la query SQL automáticamente.

**Estructura:** `findBy` + `NombreDeCampo` + condición + `And/Or` + más campos

```java
// Spring lee esto:
List<Evento> findByEstado(Estado estado);
// Y genera: SELECT * FROM eventos WHERE estado = ?

List<Evento> findByDestacado(Destacado destacado);
// Y genera: SELECT * FROM eventos WHERE destacado = ?

List<Evento> findAllByEstadoAndFechaInicioBefore(Estado estado, LocalDateTime fecha);
// Y genera: SELECT * FROM eventos WHERE estado = ? AND fecha_inicio < ?
```

El sufijo `Before` en `FechaInicioBefore` es una de las palabras clave reconocidas: Spring sabe que debe usar `<` en vez de `=`. Otras palabras clave: `After` (`>`), `Between`, `Like`, `In`, `IsNull`, `OrderBy`...

### `_` para navegar relaciones en ReservaRepository

```java
Optional<Reserva> findByEvento_IdEventoAndUsername(Long idEvento, String username);
```

El `_` le dice a Spring que `Evento` es una relación (`@ManyToOne`) y que `IdEvento` es un campo dentro de esa relación. Sin el `_`, Spring buscaría un campo llamado `eventoIdEvento` en `Reserva`, que no existe.

Spring genera: `SELECT * FROM reservas r JOIN eventos e ON r.id_evento = e.id_evento WHERE e.id_evento = ? AND r.username = ?`

### `countByUsername` — para la regla de negocio (máx. 10 reservas)

```java
int countByUsername(String username);
// SELECT COUNT(*) FROM reservas WHERE username = ?
```

En el servicio lo usaremos para comprobar que un usuario no supere las 10 reservas antes de permitir una nueva. Es más eficiente que `findByUsername(...).size()` porque no carga los objetos en memoria — solo pide el recuento a BD.

### `Optional<T>` — manejo seguro de "puede no existir"

`findById` y otros métodos que buscan un único resultado devuelven `Optional<T>` en lugar de `T` directamente. Esto fuerza a manejar el caso de que no exista:

```java
Optional<Reserva> reserva = repo.findByEvento_IdEventoAndUsername(1L, "user1");
reserva.orElseThrow(() -> new RuntimeException("No encontrado"));
// Si no existe → lanza excepción
// Si existe → devuelve el objeto Reserva
```

Esto evita `NullPointerException` silenciosos. En los servicios usaremos `orElseThrow()` para devolver un 404 cuando el recurso no se encuentre.

### ¿Por qué el tipo del segundo parámetro importa en `JpaRepository`?

```java
JpaRepository<Tipo, Long>     // PK de Tipo es Long (id_tipo)
JpaRepository<Usuario, String> // PK de Usuario es String (username)
JpaRepository<Evento, Long>    // PK de Evento es Long (id_evento)
JpaRepository<Reserva, Long>   // PK de Reserva es Long (id_reserva)
```

El segundo parámetro define el tipo que acepta `findById(id)`. Si pones `Long` pero la PK es `String`, el código compila pero falla en tiempo de ejecución. Siempre debe coincidir con el tipo del campo `@Id`.

---

## Paso 6 — Capa de servicios: `TipoService` y `UsuarioService`

### Interfaz + implementación: ¿por qué dos archivos?

El patrón estándar en Spring es definir **qué hace** el servicio (interfaz) separado de **cómo lo hace** (implementación):

```
TipoService.java        ← interfaz: declara los métodos
TipoServiceImpl.java    ← implementación: contiene la lógica
```

**¿Por qué no una clase directamente?**

1. **Inyección de dependencias limpia:** los controladores dependen de la interfaz, no de la implementación. Si mañana cambiamos la implementación (otro ORM, otra fuente de datos), el controlador no toca una línea.
2. **Testabilidad:** en tests se puede inyectar un mock de la interfaz sin necesitar la implementación real.
3. **Convención Spring:** Spring detecta la implementación anotada con `@Service` y la inyecta automáticamente donde se pida la interfaz.

### `@Service` — marcar una clase como servicio Spring

```java
@Service
public class TipoServiceImpl implements TipoService { ... }
```

`@Service` es una especialización de `@Component`. Le dice a Spring que esta clase debe ser registrada como bean en el contexto de la aplicación. Sin esta anotación, Spring no la encontraría y la inyección de dependencias fallaría.

Hay tres anotaciones similares con el mismo efecto técnico pero diferente semántica:
- `@Component` — genérico
- `@Service` — capa de lógica de negocio
- `@Repository` — capa de acceso a datos (Spring Data los registra automáticamente)
- `@Controller` / `@RestController` — capa web

### `@RequiredArgsConstructor` — inyección por constructor sin `@Autowired`

```java
@RequiredArgsConstructor
public class TipoServiceImpl implements TipoService {
    private final TipoRepository tipoRepository;
```

Lombok genera el constructor:
```java
public TipoServiceImpl(TipoRepository tipoRepository) {
    this.tipoRepository = tipoRepository;
}
```

Spring ve que hay un solo constructor y lo usa para inyectar las dependencias. Esto es **inyección por constructor**, preferida sobre `@Autowired` en campo porque:
- El campo puede ser `final` → inmutable y seguro
- Es fácil de testear (pasas el mock directamente al constructor)
- Spring mismo lo recomienda desde la versión 4.3

### `EntityNotFoundException` — excepción estándar de JPA

```java
return tipoRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Tipo no encontrado con id: " + id));
```

`EntityNotFoundException` es parte de `jakarta.persistence` — no hay que crear una excepción personalizada para este caso. En el Paso 13 (manejador global de excepciones) la capturaremos y la convertiremos en una respuesta HTTP 404.

`.orElseThrow()` es un método de `Optional`: si el Optional está vacío (no se encontró el registro), ejecuta el lambda y lanza la excepción. Si tiene valor, lo devuelve directamente.

### `deleteById` con comprobación previa

```java
public void deleteById(Long id) {
    if (!tipoRepository.existsById(id)) {
        throw new EntityNotFoundException("Tipo no encontrado con id: " + id);
    }
    tipoRepository.deleteById(id);
}
```

`JpaRepository.deleteById()` por defecto lanza `EmptyResultDataAccessException` si el ID no existe — una excepción de Spring, no de JPA. Para tener un comportamiento uniforme (siempre `EntityNotFoundException`), comprobamos antes con `existsById()`. Así el manejador global solo necesita capturar un tipo de excepción para los 404.

### `UsuarioService` — solo lectura

El APIRest no crea ni modifica usuarios — eso es responsabilidad del APILoginManager. Por eso `UsuarioService` solo expone `findByUsername`. Añadir métodos que no se usan es ruido innecesario.

---

## Paso 7 — `EventoService` y `EventoScheduler`

### `update` — patrón de actualización en JPA

Para actualizar un registro, el patrón correcto es:
1. Cargar la entidad existente de BD (con `findById`)
2. Modificar sus campos con los nuevos valores
3. Llamar a `save()` — Hibernate detecta que ya tiene `@Id` y hace `UPDATE` en lugar de `INSERT`

```java
public Evento update(Long id, Evento evento) {
    Evento existente = findById(id);   // carga de BD (lanza 404 si no existe)
    existente.setNombre(evento.getNombre());
    // ... resto de campos
    return eventoRepository.save(existente);  // UPDATE
}
```

**¿Por qué no hacer `save(evento)` directamente con el objeto que llega del controlador?**

Porque el objeto del controlador viene del JSON del cliente. Si el cliente no envía el `idEvento` correctamente, `save()` haría un `INSERT` en lugar de un `UPDATE`. Cargar primero el existente garantiza que siempre estamos modificando el registro correcto.

### `updateEstado` — endpoint específico para cambiar solo el estado

```java
public void updateEstado(Long id, Estado estado) {
    Evento evento = findById(id);
    evento.setEstado(estado);
    eventoRepository.save(evento);
}
```

Esto permite a un administrador cancelar o activar un evento sin enviar todos los datos del evento en el body. En el controlador será un `PATCH` (modificación parcial), mientras que `update` completo será un `PUT`.

### `@Scheduled` — tareas automáticas periódicas

```java
@Scheduled(fixedDelay = 60000)
public void marcarEventosTerminados() { ... }
```

`fixedDelay = 60000` significa: espera 60 000 ms (1 minuto) **desde que terminó la ejecución anterior** antes de volver a ejecutar. Alternativas:

| Parámetro | Comportamiento |
|---|---|
| `fixedDelay` | Espera N ms **tras terminar** la ejecución anterior |
| `fixedRate` | Ejecuta cada N ms **independientemente** de cuánto tarde el método |
| `cron` | Expresión cron tipo Unix: `"0 0 * * * *"` (cada hora en punto) |

Para este caso `fixedDelay` es lo más seguro: si la BD tarda más de 1 minuto en responder, no se acumulan ejecuciones paralelas.

**Importante:** este scheduler funciona porque en el Paso 1 añadimos `@EnableScheduling` en la clase principal. Sin esa anotación, `@Scheduled` es ignorado silenciosamente.

### `saveAll` — actualizar varios registros de una vez

```java
eventoRepository.saveAll(caducados);
```

En lugar de llamar a `save()` dentro de un bucle (N queries individuales), `saveAll()` agrupa las operaciones de forma más eficiente. Internamente Hibernate puede hacer batch inserts/updates según la configuración.

### Por qué el scheduler usa `EventoRepository` directamente y no `EventoService`

El scheduler podría llamar a `eventoService.updateEstado()` en un bucle, pero eso generaría N queries (una por evento). Al acceder al repositorio directamente puede usar `saveAll()` en una sola operación.

Es una excepción razonada a la regla "los componentes solo llaman a la capa de servicio". Lo importante es que la decisión esté justificada, no que la regla sea ciega.

---

## Paso 8 — `ReservaService`: reglas de negocio

### Las cuatro validaciones de `create`

Antes de guardar una reserva, el servicio comprueba cuatro condiciones en orden lógico:

```java
// 1. El evento existe y está ACTIVO
if (evento.getEstado() != Estado.ACTIVO)
    throw new IllegalStateException("No se puede reservar un evento que no está activo.");

// 2. El usuario no tiene ya una reserva para ese evento
if (reservaRepository.findByEvento_IdEventoAndUsername(idEvento, username).isPresent())
    throw new IllegalStateException("Ya tienes una reserva para este evento.");

// 3. El usuario no ha alcanzado el límite de 10 reservas
if (reservaRepository.countByUsername(username) >= 10)
    throw new IllegalStateException("Has alcanzado el límite de 10 reservas.");

// 4. El aforo del evento no está completo
if (reservaRepository.countByEvento_IdEvento(idEvento) >= evento.getAforo())
    throw new IllegalStateException("El aforo del evento está completo.");
```

El orden importa: tiene más sentido comprobar primero que el evento existe antes de consultar si el usuario ya tiene reserva en él.

### `IllegalStateException` para reglas de negocio

Se usa `IllegalStateException` (de `java.lang`, sin imports) para errores de lógica de negocio: "la operación no es válida en el estado actual del sistema". No confundir con `EntityNotFoundException` (recurso no encontrado → 404).

En el Paso 13 mapearemos:
- `EntityNotFoundException` → HTTP 404
- `IllegalStateException` → HTTP 409 Conflict (la solicitud es válida pero hay conflicto con el estado actual)
- `SecurityException` → HTTP 403 Forbidden

### `precioVenta` — captura del precio en el momento de la reserva

```java
reserva.setPrecioVenta(evento.getPrecio());
```

El precio de la reserva se guarda como una **copia** del precio del evento en ese instante. Si el precio del evento cambia después, las reservas existentes no se ven afectadas. Este es el comportamiento correcto: el usuario pagó un precio acordado, no el precio actual.

### Verificación de propiedad en `deleteById`

```java
if (!reserva.getUsername().equals(username)) {
    throw new SecurityException("No tienes permiso para cancelar esta reserva.");
}
```

Aunque Spring Security verifica que el usuario está autenticado, no comprueba que la reserva que quiere borrar sea suya. Esta validación es imprescindible: sin ella, cualquier usuario autenticado podría cancelar las reservas de otros usuarios con solo adivinar el `id`.

El `username` que llega a este método vendrá extraído del JWT en el controlador — no del cuerpo de la petición (que el cliente podría falsificar).

### Inyectar `EventoService` en vez de `EventoRepository`

```java
private final EventoService eventoService;
```

`ReservaServiceImpl` necesita buscar el evento por ID. En lugar de inyectar `EventoRepository` directamente, inyecta `EventoService` — que ya tiene el manejo de `EntityNotFoundException` centralizado. Reutilizar la capa de servicio evita duplicar lógica.

---

## Paso 10 — Controladores `TipoController` y `UsuarioController`

### `@RestController` y `@RequestMapping`

```java
@RestController          // = @Controller + @ResponseBody en todos los métodos
@RequestMapping("/tipos") // prefijo base de todas las rutas de este controlador
public class TipoController { ... }
```

`@ResponseBody` indica a Spring que el valor devuelto por cada método debe serializarse como JSON en el cuerpo de la respuesta (en lugar de interpretarse como el nombre de una vista HTML).

### `ResponseEntity<T>` — control total de la respuesta HTTP

En lugar de devolver el objeto directamente, se envuelve en `ResponseEntity` para controlar el código de estado HTTP:

```java
ResponseEntity.ok(objeto)                              // 200 OK + body
ResponseEntity.status(HttpStatus.CREATED).body(objeto) // 201 Created + body
ResponseEntity.noContent().build()                     // 204 No Content (sin body)
ResponseEntity.status(403).build()                     // 403 Forbidden (sin body)
```

El código de estado es información importante para el cliente: 201 vs 200 indica que se creó un recurso nuevo; 204 indica éxito sin datos que devolver.

### `@PathVariable` y `@RequestBody`

```java
@GetMapping("/{id}")
public ResponseEntity<Tipo> getById(@PathVariable Long id) { ... }
```

`@PathVariable` extrae un segmento de la URL y lo inyecta como parámetro. Spring convierte el String de la URL al tipo declarado (`Long` en este caso).

```java
@PostMapping
public ResponseEntity<Tipo> create(@RequestBody Tipo tipo) { ... }
```

`@RequestBody` deserializa el JSON del cuerpo de la petición a un objeto Java. Spring usa Jackson automáticamente.

### `Authentication` — obtener el usuario autenticado del token

```java
public ResponseEntity<Usuario> getByUsername(
        @PathVariable String username,
        Authentication authentication) {

    authentication.getName()          // extrae el subject del JWT (= username)
    authentication.getAuthorities()   // lista de roles del JWT
}
```

Spring inyecta el objeto `Authentication` automáticamente cuando se declara como parámetro del método. Con OAuth2 Resource Server, `getName()` devuelve el `subject` del JWT — que es el `username` del usuario autenticado.

### Control de acceso manual en `UsuarioController`

```java
boolean esAdmin = authentication.getAuthorities().stream()
        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

if (!esAdmin && !authentication.getName().equals(username)) {
    return ResponseEntity.status(403).build();
}
```

La ruta `GET /usuarios/{username}` requiere autenticación (configurado en `SecurityConfig`), pero Spring Security no sabe que un usuario solo debería ver su propio perfil. Esa regla es lógica de negocio, no de infraestructura, y se implementa en el controlador.

Un admin puede ver cualquier perfil; un usuario normal solo el suyo.

---

## Paso 11 — `EventoController` (8 endpoints)

### Resumen de rutas

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| GET | `/eventos` | Público | Todos los eventos |
| GET | `/eventos/{id}` | Público | Evento por ID |
| GET | `/eventos/estado/{estado}` | Público | Eventos filtrados por estado |
| GET | `/eventos/destacados` | Público | Eventos con destacado = S |
| POST | `/eventos` | ROLE_ADMIN | Crear evento |
| PUT | `/eventos/{id}` | ROLE_ADMIN | Actualizar evento completo |
| PATCH | `/eventos/{id}/estado` | ROLE_ADMIN | Cambiar solo el estado |
| DELETE | `/eventos/{id}` | ROLE_ADMIN | Eliminar evento |

Los permisos **no se declaran en el controlador** — ya están configurados en `SecurityConfig` por método HTTP y patrón de URL. El controlador solo gestiona la lógica HTTP.

### `@PatchMapping` vs `@PutMapping` — actualización parcial vs completa

- **`PUT`** → actualización **completa**: el cliente envía todos los campos del recurso. El servidor reemplaza el recurso entero.
- **`PATCH`** → actualización **parcial**: el cliente envía solo los campos que cambian.

`PATCH /eventos/{id}/estado` solo cambia el estado del evento sin tocar el resto de campos. Usar `PUT` para esto obligaría al cliente a enviar todos los datos del evento solo para cambiar un campo.

### `@RequestParam` para el estado en PATCH

```java
@PatchMapping("/{id}/estado")
public ResponseEntity<Void> updateEstado(
        @PathVariable Long id,
        @RequestParam Estado estado) { ... }
```

El nuevo estado llega como parámetro de query: `PATCH /eventos/5/estado?estado=CANCELADO`

Spring convierte automáticamente el String `"CANCELADO"` al enum `Estado.CANCELADO`. Si el valor no existe en el enum, Spring devuelve un `400 Bad Request` automáticamente.

### `ResponseEntity<Void>` para respuestas sin cuerpo

```java
return ResponseEntity.noContent().build(); // 204 No Content
```

`Void` indica explícitamente que no hay body. `noContent()` genera un 204, que es el código correcto para operaciones exitosas que no devuelven datos (borrados, actualizaciones parciales).

### Cómo funciona el POST con `@ManyToOne` anidado

Al crear un evento, el JSON incluye el tipo como objeto anidado:

```json
{
  "nombre": "Boda García",
  "tipo": { "idTipo": 2 },
  "precio": 1500.00,
  ...
}
```

Jackson deserializa `{ "idTipo": 2 }` como un objeto `Tipo` con solo el ID. JPA/Hibernate lo trata como una referencia a un `Tipo` existente en BD y lo usa como FK al insertar el evento — sin necesidad de cargar el `Tipo` completo.

---

## Paso 12 — `ReservaController` (3 endpoints)

### Resumen de rutas

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| GET | `/reservas/mis-reservas` | Autenticado | Reservas del usuario actual |
| POST | `/reservas/{idEvento}` | Autenticado | Crear reserva para un evento |
| DELETE | `/reservas/{id}` | Autenticado | Cancelar una reserva propia |

### El username nunca viene del cliente

```java
@PostMapping("/{idEvento}")
public ResponseEntity<Reserva> create(
        @PathVariable Long idEvento,
        Authentication authentication) {
    String username = authentication.getName(); // extraído del JWT
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(reservaService.create(username, idEvento));
}
```

El `username` se extrae siempre del JWT validado — nunca del cuerpo de la petición ni de un parámetro de URL que el cliente pueda manipular. Si se aceptara el username del cliente, cualquier usuario podría crear reservas a nombre de otro.

`authentication.getName()` devuelve el `subject` del JWT, que es el `username` del usuario autenticado tal como lo guardó el APILoginManager al generar el token.

### `POST /reservas/{idEvento}` — sin body

La ruta de creación no necesita `@RequestBody`. Toda la información necesaria está:
- `idEvento` → en la URL (`@PathVariable`)
- `username` → en el JWT (`Authentication`)
- `fechaReserva` → se asigna a `LocalDateTime.now()` en el servicio
- `precioVenta` → se copia del precio actual del evento en el servicio

Un body vacío simplifica el cliente: solo hace `POST /reservas/5` con el token en la cabecera.

### La validación de propiedad ocurre en el servicio, no aquí

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id, Authentication authentication) {
    reservaService.deleteById(id, authentication.getName());
    return ResponseEntity.noContent().build();
}
```

El controlador pasa el `username` al servicio y este comprueba que la reserva pertenece a ese usuario. La lógica de negocio está donde debe estar: en el servicio. El controlador solo traduce la petición HTTP a una llamada de servicio.

---

## Paso 13 — Manejador global de excepciones

### El problema sin `@RestControllerAdvice`

Sin un manejador global, cuando un servicio lanza una excepción Spring devuelve por defecto una respuesta genérica con HTML y código 500, independientemente de qué tipo de error sea. El cliente no sabe si el recurso no existe, si hay un conflicto de negocio o si realmente fue un error del servidor.

### `@RestControllerAdvice` — un único punto de control de errores

```java
@RestControllerAdvice
public class GlobalExceptionHandler { ... }
```

`@RestControllerAdvice` es una combinación de `@ControllerAdvice` (intercepta excepciones de todos los controladores) y `@ResponseBody` (la respuesta se serializa como JSON). Cada método anotado con `@ExceptionHandler` captura un tipo de excepción concreto.

### Mapeo de excepciones a códigos HTTP

```java
EntityNotFoundException  → 404 Not Found   (recurso no encontrado en BD)
IllegalStateException    → 409 Conflict    (regla de negocio violada)
SecurityException        → 403 Forbidden   (intento de acceder a recurso ajeno)
Exception                → 500 Internal    (cualquier error no previsto)
```

Spring evalúa los `@ExceptionHandler` de más específico a más genérico. El `Exception.class` actúa como red de seguridad para cualquier error inesperado — devuelve un mensaje genérico sin exponer detalles internos al cliente.

### Por qué el mensaje genérico en el 500

```java
@ExceptionHandler(Exception.class)
public ResponseEntity<String> handleGeneric(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error interno del servidor.");
}
```

En errores de servidor no controlados, **nunca** se devuelve `ex.getMessage()` al cliente. Los stack traces y mensajes internos son información valiosa para un atacante. El mensaje genérico informa al cliente sin revelar nada del sistema.

---

## Extra — Swagger UI con SpringDoc OpenAPI

### Dependencia

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.8.0</version>
</dependency>
```

Solo con añadir esta dependencia, SpringDoc escanea todos los `@RestController` y genera la documentación automáticamente en `/v3/api-docs` (JSON) y la UI en `/swagger-ui/index.html`.

### `SwaggerConfig` — personalizar la documentación

```java
@Bean
public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("AppEventos API").version("1.0.0"))
        .servers(List.of(
            new Server().url("http://localhost:8080").description("APIRest"),
            new Server().url("http://localhost:8082").description("APILoginManager")
        ))
        .addSecurityItem(new SecurityRequirement().addList("Bearer"))
        .components(new Components()
            .addSecuritySchemes("Bearer", new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")))
        .path("/auth/login", loginPath);  // endpoint manual del APILoginManager
}
```

**`servers`**: define los servidores disponibles en el desplegable de Swagger UI. Al añadir el APILoginManager como segundo servidor, el usuario puede llamar al login desde la misma UI sin abrir otra pestaña.

**`addSecurityItem` + `addSecuritySchemes`**: añade el candado de autenticación a todos los endpoints. El esquema `HTTP bearer` le dice a Swagger UI que debe enviar el token en la cabecera `Authorization: Bearer <token>`.

**`.path("/auth/login", loginPath)`**: documenta un endpoint manualmente en el spec OpenAPI. Se usa para el `/auth/login` del APILoginManager, que no pertenece al APIRest pero necesitamos tenerlo accesible desde su Swagger.

### Proteger Swagger con Spring Security

Sin esta configuración, Spring Security bloquearía el acceso a la UI:

```java
.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
```

Esta regla debe ir **antes** de cualquier otra en `authorizeHttpRequests` para que tenga prioridad.

### Flujo de uso para probar endpoints protegidos

1. Seleccionar servidor **`APILoginManager :8082`** en el desplegable
2. Llamar a `POST /auth/login` → copiar el token de la respuesta
3. Seleccionar de nuevo **`APIRest :8080`**
4. Clic en **Authorize** → pegar el token → **Authorize**
5. Todos los endpoints protegidos ya envían el token automáticamente

---

## Extra — CORS

### ¿Qué es CORS y por qué falla sin configurarlo?

CORS (Cross-Origin Resource Sharing) es un mecanismo de seguridad del **navegador**. Cuando el frontend (por ejemplo, en `localhost:5173`) hace una petición a una API en `localhost:8080`, el navegador bloquea la respuesta a menos que el servidor incluya las cabeceras CORS correctas.

El servidor debe indicar explícitamente qué orígenes, métodos y cabeceras están permitidos.

### Configuración en `SecurityConfig`

```java
// APIRest (puerto 8080)
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:8080"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}

// APILoginManager (puerto 8082) — debe incluir el puerto del Swagger de APIRest
@Bean
CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of(
        "http://localhost:4200",   // Angular
        "http://localhost:5173",   // Vite / frontend dev
        "http://localhost:8080",   // Swagger UI de APIRest llama a este servicio
        "http://localhost:8082"    // mismo origen para peticiones locales
    ));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    config.setAllowedHeaders(List.of("*"));

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

**Por qué APILoginManager necesita `localhost:8080` en sus orígenes permitidos:** el Swagger UI está alojado en APIRest (`localhost:8080`). Cuando el usuario hace clic en "Execute" en Swagger para llamar a `POST /auth/login`, el navegador envía esa petición desde `localhost:8080` hacia `localhost:8082`. Sin `localhost:8080` en los orígenes permitidos de APILoginManager, el navegador bloquea la respuesta con "Failed to fetch".

Y activarlo en el filtro de seguridad:
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```

**`allowedOrigins` explícitos**: cuando `allowCredentials` es `true`, no se puede usar el comodín `*` como origen — el navegador lo rechaza. Hay que listar los orígenes exactos.

**`OPTIONS`**: los navegadores envían una petición "preflight" OPTIONS antes de peticiones con cabeceras personalizadas (como `Authorization`). Si OPTIONS no está permitido, todas las peticiones con JWT fallan antes de llegar al servidor.

**`allowedHeaders(List.of("*"))`**: permite cualquier cabecera, incluyendo `Authorization` (necesaria para el JWT).

### CORS en Spring Security vs `@CrossOrigin`

`@CrossOrigin` en el controlador funciona para MVC simple, pero en presencia de Spring Security **hay que configurar CORS en el filtro de seguridad**. De lo contrario, Spring Security intercepta la petición antes de que llegue al controlador y la bloquea sin aplicar las cabeceras CORS — el navegador recibe un 401/403 sin las cabeceras correctas y lo interpreta como error CORS.

---

## Extra — Endpoint `GET /reservas/evento/{idEvento}`

Añadido para que los administradores puedan ver qué usuarios han reservado un evento concreto. Útil para gestión de asistencia y control de aforo.

| Método | Ruta | Acceso | Descripción |
|--------|------|--------|-------------|
| GET | `/reservas/evento/{idEvento}` | ROLE_ADMIN | Todas las reservas de un evento |

La regla de seguridad se declaró explícitamente en `SecurityConfig` antes de `anyRequest().authenticated()`:

```java
.requestMatchers(HttpMethod.GET, "/reservas/evento/**").hasAuthority("ROLE_ADMIN")
```

Sin esta línea, cualquier usuario autenticado podría ver las reservas de otros — `anyRequest().authenticated()` solo exige estar logado, no ser admin.

---

## Extra — Sincronización de claves JWT entre dos APIs

Cuando hay dos servicios independientes que firman y verifican JWT respectivamente, **ambos deben usar exactamente la misma clave secreta**. Si la clave de firma (APILoginManager) y la de verificación (APIRest) son distintas, todos los tokens devolverán 401 aunque el token sea estructuralmente correcto.

### Síntoma

```
401 Unauthorized
www-authenticate: Bearer resource_metadata="http://localhost:8080/.well-known/oauth-protected-resource"
```

Y en la consola del servidor:

```
AuthenticationServiceException: An error occurred while attempting to decode the Jwt:
The secret length for HS384 must be at least 384 bits
```

El segundo error indica que la clave que está leyendo APIRest tiene menos bytes de los necesarios para el algoritmo HS384, lo que confirma que está leyendo una clave diferente (la original, más corta).

### Causa habitual: clave diferente en cada `application.properties`

```properties
# APILoginManager/src/main/resources/application.properties
jwt.secret=/sWwResFLrnjR1XEEMShseNxWVRUAgeiAdeKvCsZrwZ3LTzuQhdK4vD+jx5NyMId

# APIRest/src/main/resources/application.properties  ← debe ser IDÉNTICA
jwt.secret=/sWwResFLrnjR1XEEMShseNxWVRUAgeiAdeKvCsZrwZ3LTzuQhdK4vD+jx5NyMId
```

La clave es un secreto en Base64. Ambas APIs la decodifican con `Base64.getDecoder().decode()` y obtienen los mismos bytes → misma clave HMAC → las firmas coinciden.

### Cómo generar una clave válida para HS384

El proyecto incluye `GeneradorClave384bits.java` en APILoginManager. Ejecutarlo imprime por consola una clave Base64 válida de 384 bits lista para copiar en ambos archivos:

```java
SecretKey key = Jwts.SIG.HS384.key().build();
System.out.println(Encoders.BASE64.encode(key.getEncoded()));
```

Requisito mínimo para HS384: **48 bytes = 384 bits** después del decode Base64. Con menos bytes, Nimbus JOSE (la librería interna de Spring Security) rechaza el token.

### El token sigue siendo rechazado después de cambiar la clave

En Eclipse/STS, el Spring Boot Dashboard ejecuta la aplicación desde `target/classes/`, **no** desde `src/main/resources/`. Si solo editas el archivo fuente sin recompilar, el servidor sigue leyendo la clave antigua del directorio compilado.

**Solución: Maven clean → install antes de reiniciar**

```
Clic derecho en el proyecto → Run As → Maven clean
Clic derecho en el proyecto → Run As → Maven install (o -DskipTests)
Reiniciar desde el Spring Boot Dashboard
```

Desde terminal:
```bash
mvn clean install -DskipTests
```

Esto aplica a **cualquier cambio en `application.properties`**: la propiedad nueva no tiene efecto hasta que Maven copia el archivo actualizado a `target/classes/`.

---

## Extra — Path matching en Spring Security: `/**` no cubre la ruta raíz

Un patrón muy habitual de error al configurar rutas públicas:

```java
// INCOMPLETO — cubre /tipos/1, /tipos/abc, pero NO /tipos
.requestMatchers(HttpMethod.GET, "/tipos/**").permitAll()
```

En Spring Security, `/**` requiere al menos un segmento después de la barra. La ruta `/tipos` (sin nada después) no hace match con `/tipos/**` y cae al siguiente matcher — en este proyecto, `.anyRequest().authenticated()`.

Resultado: `GET /tipos` devuelve 401 aunque sea un endpoint público.

**Corrección: listar explícitamente la ruta raíz junto al wildcard**

```java
.requestMatchers(HttpMethod.GET, "/tipos", "/tipos/**").permitAll()
.requestMatchers(HttpMethod.GET, "/eventos", "/eventos/**").permitAll()
```

Esto cubre todos los casos:
- `/tipos` → lista completa (sin path variable)
- `/tipos/1` → detalle por ID
- `/tipos/algo/mas` → cualquier subruta

### El 401 que confunde: token inválido en ruta pública

Otro comportamiento no obvio: si envías un `Authorization: Bearer <token>` con firma inválida a una ruta `permitAll()`, Spring Security devuelve **401 igualmente**. El middleware de OAuth2 Resource Server intenta validar el token **antes** de comprobar si la ruta es pública. Si el token falla la validación, rechaza la petición sin llegar a evaluar los permisos de la ruta.

Para endpoints verdaderamente públicos, la forma más limpia de probarlos es sin cabecera `Authorization`. Si devuelven 200 sin token, la configuración de rutas es correcta; si devuelven 401 con un token inválido, el problema es la clave JWT, no los permisos.
