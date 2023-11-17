# Spring Data Tarantool

[![cartridge-springdata:ubuntu/master Actions Status](https://github.com/tarantool/cartridge-springdata/workflows/ubuntu-master/badge.svg)](https://github.com/tarantool/cartridge-springdata/actions)

The primary goal of the [Spring Data](https://projects.spring.io/spring-data)
project is to make it easier to build Spring-powered applications that
use new data access technologies such as non-relational databases,
map-reduce frameworks, and cloud based data services.

The Spring Data Tarantool project provides Spring Data way of working
with Tarantool database spaces. The key features are repository support
and different API layers which are common in Spring Data, as well as
support for asynchronous approaches baked by the underlying asynchronous
Tarantool database driver.

## Spring Boot compatibility

| `spring-data-tarantool` Version | Spring Boot Version |
|:--------------------------------|:-------------------:|
| 0.x.x                           |        2.2.x        |
| 0.3.x                           |        2.3.2        |
| 0.5.2                           |        2.6.3        |

## Tarantool compatibility

| `spring-data-tarantool` Version | Tarantool Version |
|:--------------------------------|:-----------------:|
| 0.x.x                           |    1.10.x, 2.x    |

## References

The Tarantool Database documentation is located at
[tarantool.io](https://www.tarantool.io/en/doc/latest/reference/)

Feel free to join the [Tarantool community chat](https://t.me/tarantool)
in Telegram (or its counterpart [in Russian](https://t.me/tarantoolru))
if you have any questions about Tarantool database or Spring Data Tarantool.

Detailed questions can be asked on StackOverflow using the
[tarantool](https://stackoverflow.com/questions/tagged/tarantool) tag.

Documentation and StackOverflow links will be added in the nearest future.

If you are new to Spring as well as to Spring Data, look for information
about [Spring projects](https://projects.spring.io/).

## Quick Start

### Demo project

Check out a traditional [Pet Clinic application](https://github.com/tarantool/spring-petclinic-tarantool) implemented using this module.

### Maven configuration

Add the Maven dependency:

```xml
<dependency>
    <groupId>io.tarantool</groupId>
    <artifactId>spring-data-tarantool</artifactId>
    <version>0.6.1</version>
</dependency>
```

### TarantoolTemplate

`TarantoolTemplate` is the central support class for Tarantool database
operations. It provides:

* Basic POJO mapping support to and from Tarantool tuples
* Convenience methods to interact with the store (insert object,
update objects, select)
* Exception translation into Spring's
[technology agnostic DAO exception hierarchy](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/dao.html#dao-exceptions).

### Spring Data repositories

To simplify the creation of data repositories Spring Data Tarantool
provides a generic repository programming model. It will automatically
create a repository proxy for you that adds implementations of finder
methods you specify on an interface.

Only simple CRUD operations including entity ID are supported at the
moment (will be fixed soon).

For example, given a `Book` class with name and author properties, a
`BookRepository` interface can be defined for saving and loading the
entities:

```java
@Tuple("books")
public class Book {
    @Id
    private Integer id;

    @Field(name = "unique_key")
    private String uniqueKey;

    @Field(name = "book_name")
    private String name;

    private String author;

    private Integer year;
}
```

```java
public interface BookRepository extends CrudRepository<Book, Long> {
}
```

The `@Tuple` annotation allows to specify the space name which schema will be used for forming the data tuples to sending
and for mapping the incoming tuple into an object.
You can omit the `@Tuple` annotation, then the connector will take the class name converted to snake_case.
The `@Field` annotations provide custom names for the fields. It is necessary to have at least 
one field marked with the `@Id` annotation.

Extending `CrudRepository` causes basic CRUD methods being pulled into the
interface so that you can easily save and find single entities and
collections of them. We can also use Tarantool specific interface `TarantoolRepository`.

Let's assume that you have the [tarantool/crud](https://github.com/tarantool/crud) module installed in yor Cartridge
application and there is an active 'crud-router' role on your router. Put the following settings in your application
properties:

| Property name      | Example value        | Description                                                          |
|:-------------------|:---------------------|:---------------------------------------------------------------------|
| tarantool.host     | localhost            | Cartridge router host                                                |
| tarantool.port     | 3301                 | Cartridge router port                                                |
| tarantool.username | admin                | Default username for API access, may be different                    |
| tarantool.password | myapp-cluster-cookie | Password for API access, see you Cartridge application configuration |

Than you can have Spring automatically create a proxy for the repository interface by using the following JavaConfig:

```java
@Configuration
@EnableTarantoolRepositories(basePackageClasses = BookRepository.class)
class ApplicationConfig extends AbstractTarantoolDataConfiguration {

    @Value("${tarantool.host}")
    protected String host;
    @Value("${tarantool.port}")
    protected int port;
    @Value("${tarantool.username}")
    protected String username;
    @Value("${tarantool.password}")
    protected String password;

    @Override
    protected TarantoolServerAddress tarantoolServerAddress() {
        return new TarantoolServerAddress(host, port);
    }

    @Override
    public TarantoolCredentials tarantoolCredentials() {
        return new SimpleTarantoolCredentials(username, password);
    }

    @Override
    public TarantoolClient tarantoolClient(TarantoolClientConfig tarantoolClientConfig,
                                           TarantoolClusterAddressProvider tarantoolClusterAddressProvider) {
        return new ProxyTarantoolTupleClient(super.tarantoolClient(tarantoolClientConfig, tarantoolClusterAddressProvider));
    }
}
```

This sets up a connection to a local Tarantool instance and enables the
detection of Spring Data repositories (through `@EnableTarantoolRepositories`).

This will find the repository interface and register a proxy object in the container. You can use it as shown below:

```java
@Service
public class MyService {

    private final BookRepository repository;

    @Autowired
    public MyService(BookRepository repository) {
        this.repository = repository;
    }

    public void doWork() {
        Book book = new Book();
        book.setName("Le Petit Prince");
        book.setAuthor("Antoine de Saint-Exupéry");
        Book savedBook = repository.save(book);

        List<Book> allBooks = repository.findAll();
    }
}
```

#### Proxy Tarantool functions in repositories

Consider we need to write a complex query in Lua, working with sharded data in Tarantool Cartridge. In this case
we can expose this query as a public API function and map that function on a repository method via the `@Query`
annotation by specifying the functionName parameter:

```java
public interface BookRepository extends CrudRepository<Book, Long> {
    @Query(function = "find_by_complex_query")
    List<Book> findByYearGreaterThenProxy(Integer year);
}
```

The corresponding function in on Tarantool Cartridge router may look like (uses the
[tarantool/crud](https://github.com/tarantool/crud) module):

```lua
    local crud = require('crud')
    local fun = require('fun')

    ...

    function find_by_complex_query(year)
        return crud.pairs('books'):filter(function(b) return b[6] and b[6] > year end):totable()
    end
```

See more examples in the module tests.

##### Specify output
For such methods, you can specify the stored function response format so that it will be parsed correctly.
The response format can be specified in the `@Query` annotation using the `output` parameter, see the examples below.

```java
public interface SampleUserRepository extends TarantoolRepository<SampleUser, String> {
    @Query(function = "get_users_with_age_gt", output = TarantoolSerializationType.TUPLE)
    List<SampleUser> usersWithAgeGreaterThen(Integer age);

    @Query(function = "get_predefined_user", output = TarantoolSerializationType.AUTO)
    SampleUser predefinedUser();
}
```

```lua
function get_users_with_gt_age(age)
    return crud.select("sample_user", { { ">", "age", age } })
end

function get_predefined_user()
    return { name = "John", age = 46 }
end
```
The first function returns users who are older than the specified age.
In the Repository, we indicate that we expect `TUPLE`, because `crud.select` returns the result in compressed format, flatten tuples.
The keys for mapping the result into a Java object are obtained from the space metadata determined by the class name in the method return type. If this class has a `@Tuple` annotation with a custom space name, this space will be used for getting the metadata.

The second function returns a ready-made result with keys.
So `TUPLE` will not work here, because it's not flatten structure, and we specify `AUTO`.
Mapping happens by keys of table.

Of course, the word `AUTO` means that we can accept any result, and therefore it can be specified in the first request as well.
But we specify `TUPLE` because a different stack of converters is used and the conversion is faster.
For ease of use and clarity, the **default output parameter** is `TarantoolSerializationType.AUTO`.

We can also return primitive types if needed:
```java
@Query(function = "get_age_by_name", output = TarantoolSerializationType.AUTO)
Optional<Integer> getAgeByName(String name);
```

```lua
function get_age_by_name(name)
    local user = crud.get("sample_user", name)
    if user.rows[1] ~= nil then
        return user.rows[1][2]
    end
    return nil
end
```


### Composite primary key

You can create an entity representing a Tarantool tuple with composite primary index. For this you need to use
@TarantoolIdClass annotation on entity to specify the type of id. Also you may mark all 'id' fields in the entity with
standard @Id annotation. @Id annotation on properties is optional but It is recommended to use it to make code more
clear.

See the example:

```java

public class BookTranslationId {
    private Integer bookId;
    private String language;
    private Integer edition;
}

@Tuple("book_translation")
@TarantoolIdClass(BookTranslationId.class)
public class BookTranslation {
    @Id
    @Field(value = "id")
    private Integer bookId;
    @Id
    private String language;
    @Id
    private Integer edition;
    private String translator;
    private String comments;
}

public interface BookTranslationRepository
        extends TarantoolRepository<BookTranslation, BookTranslationId> {
}

```

## Contributing to Spring Data Tarantool

Contributions and issues are welcome, feel free to add them to this project or offer directly in the Tarantool community
chat or on StackOverflow using the [tarantool](https://stackoverflow.com/questions/tagged/tarantool)
tag.
