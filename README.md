# Pool de Conexiones - Transacciones Entre Dos Cuentas

## Información de la actividad
- **Universidad:** Universidad de La Sabana  
- **Facultad:** Facultad de Ingeniería  
- **Materia:** Patrones Arquitectónicos Avanzados 
- **Profesor:** Daniel Orlando Saavedra Fonnegra

## Integrantes del Proyecto
| Nombre | Correo Electrónico |
|--------|-------------------|
| Juan David Cetina Gómez | juancego@unisabana.edu.co |
| Ana Lucía Quintero Vargas | anaquiva@unisabana.edu.co |

## Estructura de la Documentación
- [1. Abstract](#1-abstract)
- [2. Introducción](#2-introducción)
- [3. Metodología](#3-metodología)
- [4. Análisis y Resultados](#4-análisis-y-resultados)
- [5. Conclusión](#5-conclusión)
- [6. Referencias](#6-referencias)

---

## 1. Abstract

This project analyzes the behavior of a Spring Boot REST API under concurrent load, focusing on how different HikariCP connection pool configurations affect performance. A basic banking simulation was developed where two accounts repeatedly transfer money to each other. To ensure data consistency during simultaneous access, optimistic locking with the @Version annotation and automatic retries using @Retryable were implemented. Load testing with Apache JMeter and performance monitoring with New Relic enabled real-time observation of system behavior under 30 concurrent threads. Four connection pool setups were tested, and results showed that increasing the pool size improves response times and reduces conflicts up to a certain threshold. In this case, the 50-20 configuration achieved the best balance between speed and resource usage, making it the most efficient despite exceeding the number of concurrent users.

**Keywords:** Spring Boot, HikariCP, concurrency, optimistic locking, load testing, REST API, JPA, JMeter, New Relic.

---

## 2. Introducción

El propósito principal de este proyecto fue analizar cómo se comporta una aplicación cuando muchos usuarios realizan operaciones simultáneamente. Para ello, se diseñó una simulación bancaria donde dos cuentas pueden enviarse dinero entre sí. Aunque la lógica del sistema era sencilla, el énfasis estuvo en observar cómo respondía bajo presión, especialmente al modificar la cantidad de conexiones activas entre la aplicación y la base de datos.

Se utilizó Java con Spring Boot para desarrollar la aplicación, y una base de datos en la nube para almacenar la información [1]. Además, se implementaron mecanismos como el control de concurrencia optimista y los reintentos automáticos, permitiendo manejar situaciones donde varias transacciones intentan modificar los mismos datos al mismo tiempo, gracias a funcionalidades del framework JPA y la anotación <code>@Retryable</code> [1].

El desempeño del sistema se midió usando dos herramientas: Apache JMeter, que simula múltiples usuarios ejecutando transacciones de forma concurrente, y New Relic, que permite monitorear en tiempo real el tiempo de respuesta de cada parte del sistema [2]. Con esto, se probaron distintas configuraciones del número de conexiones disponibles, desde muy pocas hasta muchas, para identificar cuál ofrecía el mejor rendimiento en un entorno de alta concurrencia.

---

## 3. Metodología

<p align="justify">
Con el fin de evaluar el comportamiento concurrente de una API REST desarrollada en Spring Boot, se diseñó una práctica centrada en probar cómo distintas configuraciones del pool de conexiones HikariCP afectaban la interacción entre el backend Java y una base de datos remota alojada en Railway. El enfoque principal fue observar, mediante pruebas de carga, cómo se distribuían los tiempos de respuesta entre la lógica de la aplicación y el acceso a la base de datos, bajo diferentes escenarios de concurrencia.
</p>

<p align="justify">
Para ello, se generó un proyecto base utilizando Spring Initializr, en el cual se implementaron las entidades necesarias (cuentas y transacciones), junto con sus respectivos DTOs, repositorios JPA, entidades ORM y controladores REST. Por un lado, la tabla de cuentas incluyó columnas para id (autogenerada), monto y un campo adicional service añadido para gestionar correctamente accesos simultáneos. Para resolver posibles conflictos al modificar datos compartidos entre múltiples hilos, se implementó control de concurrencia optimista mediante el uso de una anotación @Version, que permite a JPA detectar si otra transacción ha modificado el mismo registro en paralelo y, en caso de conflicto, lanzar una excepción. 
</p>

<p align="justify">
Dicho manejo se complementó con la anotación <code>@Retryable</code>, configurado para reintentar hasta tres veces con pausas de 100 ms. Además, se usó <code>@Transactional</code> para asegurar la atomicidad del proceso de transferencia de fondos, evitando enfoques más restrictivos como <code>synchronized</code>.
</p>

<p align="justify">
Por otro lado, la tabla de transacciones incluía los campos id, origen_id, destino_id, monto y timestamp, representando cada operación entre dos cuentas. La base de datos fue generada automáticamente gracias a la propiedad ddl-auto: update, eliminando la necesidad de scripts SQL manuales.
</p>

<p align="justify">
En cuanto a la funcionalidad expuesta por la API, se creó un endpoint para inicializar dos cuentas con 10,000 pesos, y otro para listar todas las cuentas existentes. Estas operaciones fueron clave para observar los efectos de la concurrencia en tiempo real.
</p>

<p align="justify">
Para el monitoreo, se integró New Relic, instalando los agentes requeridos y configurando el archivo de conexión. La aplicación se ejecutó localmente con el agente activado, lo que permitió recolectar métricas de desempeño durante las pruebas de carga:
</p>

```
& "C:\Program Files\Java\jdk-17\bin\java.exe" -javaagent:"C:\Users\analu\Downloads\banco\banco\newrelic\newrelic.jar" -jar "build\libs\banco-0.0.1-SNAPSHOT.jar"
```
<p align="justify">
Las pruebas se realizaron con Apache JMeter utilizando 30 hilos concurrentes. Inicialmente se estableció un número finito de repeticiones, pero al no alcanzar los resultados esperados, se optó por usar un bucle infinito en los hilos. Cada hilo ejecutaba múltiples transacciones <code>POST</code> que simulaban el envío de 5 unidades monetarias desde la cuenta 1 hacia la cuenta 2. Para que el backend procesara correctamente estas solicitudes, se configuró el encabezado <code>Content-Type: application/json</code>, asegurando que los datos fueran enviados en el formato adecuado. A partir de ahí, se monitoreó manualmente la evolución de los saldos, verificando que las transacciones se ejecutaran correctamente hasta que una cuenta llegara a cero y la otra a 20,000 pesos.
</p>

<p align="justify"> Cabe resaltar que, entre cada configuración de pruebas, se utilizó Postman para reiniciar los saldos de ambas cuentas a 10,000 pesos, asegurando condiciones iniciales consistentes. Adicionalmente, se realizó una nueva conexión a New Relic antes de cada prueba, de forma que los datos registrados reflejaran únicamente la ejecución correspondiente a la configuración evaluada. </p>

<p align="justify">
Para el análisis de rendimiento, se evaluaron cuatro configuraciones del pool de conexiones: básica, intermedia, agresiva y muy agresiva. Todas compartían los mismos valores de idleTimeout (30,000 ms), maxLifetime (1,800,000 ms) y connectionTimeout (30,000 ms), parámetros que se mantuvieron constantes para que las diferencias observadas se debieran únicamente a la variación en el tamaño del pool.
</p>

<p align="justify">
En cuanto a los parámetros variables, maximum-pool-size y minimum-idle, se definieron en cuatro niveles: 5-2 (básica), 20-5 (intermedia), 50-20 (agresiva) y 80-40 (muy agresiva). Estos valores fueron seleccionados estratégicamente para representar distintos grados de disponibilidad de conexiones ante la carga concurrente generada por los 30 hilos activos de JMeter. La configuración básica, con un máximo de 5 conexiones y solo 2 en estado inactivo, fue pensada para simular un sistema con recursos limitados, en el que se espera cierto grado de contención. La configuración intermedia amplió estos valores a 20-5, buscando un equilibrio más razonable entre disponibilidad y consumo de recursos. En la configuración agresiva (50-20), se permitió un margen mucho mayor para atender múltiples solicitudes concurrentes sin generar tiempos de espera elevados, ideal para analizar el desempeño en un escenario más exigente. Finalmente, la configuración muy agresiva (80-40) se planteó como un límite superior, incluso mayor al número de usuarios concurrentes, con el fin de verificar si un aumento significativo en las conexiones disponibles se traducía realmente en mejoras perceptibles en los tiempos de respuesta o si, por el contrario, implicaba un uso innecesario de recursos.
</p>

---

## 4. Análisis y Resultados

<p align="justify">
A continuación, en las gráficas de las Figuras 1, 2, 3 y 4 se observan el comportamiento de los tiempos de respuesta durante una carga concurrente generada por JMeter, donde cada hilo ejecuta múltiples transacciones POST hacia el backend. En este caso, se configuró para que en todas las configuraciones desde el JMeter se tengan 30 usuarios (hilos) con un período de arranque (Ramp-up period) de 1 segundo para realizar de manera adecuada los test de concurrencia.
</p>

<p align="justify">
En cada gráfica se puede observar que las áreas coloreadas representan el tiempo consumido por la aplicación Java (azul) y por la base de datos MySQL (verde), permitiendo analizar la distribución del tiempo de procesamiento y el impacto de cada configuración.
</p>

###  Pool con Configuración Básica

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 5](Diagramas/5maxPool.png)

*Figura 1. Visualización del tiempo de transacción web obtenida desde New Relic con una configuración básica de HikariCP, con un maximumPoolSize de 5 y un minimumIdle de 2.*

<p align="justify">
En esta gráfica se ve que la mayoría del tiempo lo consume la aplicación en Java. Esto indica que hay muchas operaciones esperando una conexión disponible, lo que genera un cuello de botella en el backend. El sistema no es capaz de atender bien a los 30 usuarios simultáneos.
</p>

### Pool con Configuración Intermedia

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 20](Diagramas/20maxPool.png)

*Figura 2. Visualización del tiempo de transacción web obtenida desde New Relic con una configuración intermedia de HikariCP, con un maximumPoolSize de 20 y un minimumIdle de 5.*

<p align="justify">
En esta prueba se evidencia una mejora significativa en los tiempos del backend. Aunque la base de datos comenzó a asumir una proporción mayor del tiempo de respuesta, Java continuó siendo el componente más exigido, aunque en menor medida que en la configuración básica.
</p>

### Pool con Configuración Agresiva

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 50](Diagramas/50maxPool.png)

*Figura 3. Visualización del tiempo de transacción web obtenida desde New Relic con una configuración agresiva de HikariCP, estableciendo un maximumPoolSize de 50 y un minimumIdle de 20.*

<p align="justify">
En este caso se observó una reducción considerable en los tiempos del backend, y un incremento progresivo en el peso relativo de la base de datos en el tiempo total de transacción. Aunque la carga comenzaba a repartirse de forma más equilibrada entre los componentes, Java seguía teniendo una ligera predominancia. Aun siendo esta la configuración con mejor desempeño hasta el momento, se consideró relevante evaluar un escenario aún más exigente, para observar si una mayor cantidad de conexiones podría finalmente desplazar el cuello de botella hacia la base de datos y así optimizar aún más el rendimiento general.
</p>

### Pool con Configuración Muy Agresiva

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 80](Diagramas/80maxPool.png)

*Figura 4. Visualización del tiempo de transacción web obtenida desde New Relic bajo una configuración muy agresiva de HikariCP, con un maximumPoolSize de 80 y un minimumIdle de 40.*

<p align="justify">
En este escenario, la aplicación dispone de suficientes conexiones para atender a todos los usuarios sin demoras visibles. No obstante, el tiempo de respuesta total deja de mejorar significativamente en comparación con la configuración anterior. Incluso se evidencia un uso innecesario de recursos, ya que se asignan más conexiones de las realmente requeridas. A diferencia de las pruebas previas, el objetivo aquí fue explorar si un mayor número de conexiones podía trasladar el cuello de botella hacia la base de datos, aliviando así la presión sobre Java. 
</p>

<p align="justify">
Con base en las comparaciones realizadas, se puede concluir que aumentar el número de conexiones disponibles mejora el desempeño hasta cierto punto. Sin embargo, más allá de un nivel intermedio, los beneficios se vuelven marginales y pueden implicar un desperdicio de recursos. Por ello, resulta más recomendable optar por una configuración equilibrada (como la intermedia o la agresiva), que favorezca la eficiencia sin sobrecargar el entorno. Curiosamente, en este caso particular, fue la configuración agresiva la que arrojó los mejores tiempos de respuesta, posiblemente debido a que la base de datos estaba alojada en Railway, una plataforma capaz de manejar eficientemente múltiples conexiones activas y de sostener un alto nivel de concurrencia sin degradación del servicio. 
</p> 

<p align="justify">
Además, se pudo verificar que la estrategia de control de concurrencia mediante el campo adicional <code>service</code> dentro de la entidad <code>Cuenta</code>, combinada con el uso de anotaciones como <code>@Transactional</code> y <code>@Retryable</code>, logró mantener la consistencia de las transacciones. Esto se reflejó en que, durante los test de carga, solo una fracción de las solicitudes simultáneas lograba ejecutar la transacción exitosamente, mientras que las demás eran rechazadas con errores como <code>OptimisticLockException</code> u <code>ObjectOptimisticLockingFailureException</code>. Esto validó que la lógica de control concurrente era efectiva al evitar condiciones de carrera, sin necesidad de aplicar soluciones menos escalables como <code>synchronized</code>.
</p>

---

## 5. Conclusión

<p align="justify">
Este proyecto permitió entender y observar cómo el número de conexiones disponibles entre una aplicación y su base de datos influye directamente en el rendimiento general del sistema. Se comprobó que, cuando hay pocas conexiones, el sistema se vuelve lento y no puede atender correctamente a muchos usuarios. Al aumentar ese número se mejoran los tiempos de respuesta y se reducen los bloqueos, aunque solo hasta cierto punto.
</p>

<p align="justify">
Para garantizar la integridad de las transacciones durante cargas concurrentes, se implementaron estrategias como el uso de anotaciones <code>@Transactional</code> y <code>@Retryable</code>, además de una lógica personalizada con un campo adicional que permitió evitar conflictos sin recurrir a sincronización explícita. Estas decisiones contribuyeron a una solución robusta, capaz de operar de manera segura en entornos altamente concurrentes.
</p>

<p align="justify">
El uso combinado de herramientas como JMeter y New Relic facilitó una evaluación visual y precisa del comportamiento del sistema frente a distintas configuraciones del pool de conexiones. Las gráficas permitieron observar cómo se distribuía el tiempo de procesamiento entre Java y MySQL, evidenciando que el desempeño mejora hasta un punto intermedio, más allá del cual los beneficios son marginales y puede haber uso innecesario de recursos.
</p>

<p align="justify">
En ese sentido, la configuración con <code>maximumPoolSize</code> de 50 y <code>minimumIdle</code> de 20 se destacó como la más equilibrada: ofreció los mejores tiempos de respuesta sin sobrecargar el entorno, logrando una distribución armónica de carga entre aplicación y base de datos. En conjunto, estos hallazgos validan que una adecuada configuración del pool, sumada a una lógica de concurrencia bien diseñada, permite construir sistemas más eficientes, consistentes y escalables.
</p>

---

## 6. Referencias

[1] Spring, “Spring Framework Documentation.” [Online]. Available: https://docs.spring.io/spring-framework/

[2] New Relic, “New Relic Documentation.” [Online]. Available: https://docs.newrelic.com/
