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
- [1. Introducción](#1-introducción)
- [2. Metodología](#2-metodología)
- [3. Análisis y Resultados](#3-análisis-y-resultados)
- [4. Conclusión](#4-conclusión)
- [5. Referencias](#5-referencias)

---

## 1. Introducción



---

## 2. Metodología

Con el fin de evaluar el comportamiento concurrente de una API REST desarrollada en Spring Boot, se diseñó una práctica centrada en probar cómo distintas configuraciones del pool de conexiones HikariCP afectaban la interacción entre el backend Java y una base de datos remota alojada en Railway. El enfoque principal fue observar, mediante pruebas de carga, cómo se distribuían los tiempos de respuesta entre la lógica de la aplicación y el acceso a la base de datos, bajo diferentes escenarios de concurrencia.

Para ello, se generó un proyecto base utilizando Spring Initializr, en el cual se implementaron las entidades necesarias (cuentas y transacciones), junto con sus respectivos DTOs, repositorios JPA, entidades ORM y controladores REST. Por un lado, la tabla de cuentas contenía columnas para id (autogenerada), monto y un campo adicional service añadido para gestionar correctamente accesos simultáneos. Esta estrategia ofreció una solución más robusta al problema de concurrencia que otras opciones como el uso de synchronized, permitiendo mayor control a nivel de lógica de negocio.

Por otro lado, la tabla de transacciones incluía los campos id, origen_id, destino_id, monto y timestamp, representando cada operación entre dos cuentas. La estructura de la base de datos fue generada automáticamente gracias a la configuración ddl-auto: update de Spring Data JPA, lo cual permitió crear y actualizar las tablas sin necesidad de scripts SQL manuales.

En cuanto a la funcionalidad expuesta por la API, se creó un endpoint que permitía inicializar dos cuentas con un monto de 10,000 pesos cada una, y otro que permitía listar todas las cuentas existentes. Estas operaciones fueron clave para monitorear los cambios de saldo durante las pruebas de concurrencia.

Para realizar el monitoreo en tiempo real del rendimiento, se integró la aplicación con la plataforma New Relic. Se completaron los pasos de instalación de logs, agente de infraestructura y agente Java, configurando el archivo de New Relic para que identificara la aplicación, el cual tenía el nombre de “banco”. Posteriormente, la aplicación se ejecutó localmente a través de un comando que incorporaba el agente, permitiendo que los datos de desempeño se enviaran directamente a la plataforma de monitoreo.

'''
& "C:\Program Files\Java\jdk-17\bin\java.exe" -javaagent:"C:\Users\analu\Downloads\banco\banco\newrelic\newrelic.jar" -jar "build\libs\banco-0.0.1-SNAPSHOT.jar"
'''

Las pruebas de carga se ejecutaron utilizando Apache JMeter con 30 hilos concurrentes. Inicialmente se estableció un número finito de repeticiones, pero al no alcanzar los resultados esperados, se optó por usar un bucle infinito en los hilos. A partir de ahí, se monitoreó manualmente la evolución de los saldos, verificando que las transacciones se ejecutaran correctamente hasta que una cuenta llegara a cero y la otra a 20,000 pesos.

Para el análisis de rendimiento, se probaron cuatro configuraciones distintas del pool de conexiones HikariCP: básica, intermedia, agresiva y muy agresiva. Todas compartían los mismos valores de idleTimeout (30,000 ms), maxLifetime (1,800,000 ms) y connectionTimeout (30,000 ms), pero se diferenciaban por los valores asignados a maximum-pool-size y minimum-idle. Cada configuración fue evaluada con los mismos 30 usuarios concurrentes, permitiendo comparar cómo cambiaban las proporciones de tiempo consumido por el backend frente al uso de la base de datos en cada caso. Esto permitió determinar cuál configuración era más equilibrada en cuanto a uso de recursos y tiempo de respuesta.

---

## 3. Análisis y Resultados

A continuación, en las gráficas de las Figuras 1, 2, 3 y 4 se observan el comportamiento de los tiempos de respuesta durante una carga concurrente generada por JMeter, donde cada hilo ejecuta múltiples transacciones POST hacia el backend. En este caso, se configuró para que en todas las configuraciones desde el JMeter se tengan 30 usuarios (hilos) con un período de arranque (Ramp-up period) de 1 segundo para realizar de manera adecuada los test de concurrencia.

En cada gráfica se puede observar que las áreas coloreadas representan el tiempo consumido por la aplicación Java (azul) y por la base de datos MySQL (verde), permitiendo analizar la distribución del tiempo de procesamiento y el impacto de cada configuración.

###  Pool con Configuración Básica

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 5](Diagramas/5maxPool.png)

*Figura 1. Visualización del tiempo de transacción web obtenida desde New Relic con una configuración básica de HikariCP, con un maximumPoolSize de 5 y un minimumIdle de 2.*

En este caso, se observaron tiempos elevados del lado de la aplicación Java en comparación con la base de datos. Esta configuración limitada permitió identificar claramente un cuello de botella en el backend, lo que provocó un predominio del consumo de recursos por parte de Java frente a MySQL.

### Pool con Configuración Intermedia

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 20](Diagramas/20maxPool.png)

*Figura 2. Visualización del tiempo de transacción web obtenida desde New Relic con una configuración intermedia de HikariCP, con un maximumPoolSize de 20 y un minimumIdle de 5.*

En esta prueba se evidenció una mejora significativa en los tiempos del backend Java. Aunque la base de datos comenzó a asumir una proporción mayor del tiempo de respuesta, Java continuó siendo el componente más exigido, aunque en menor medida que en la configuración básica.


### Pool con Configuración Agresiva

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 50](Diagramas/50maxPool.png)

*Figura 3. Visualización del tiempo de transacción web obtenida desde New Relic con una configuración agresiva de HikariCP, estableciendo un maximumPoolSize de 50 y un minimumIdle de 20.*

Se observó una reducción considerable en los tiempos del backend, y un incremento progresivo en el peso relativo de la base de datos en el tiempo total de transacción. Si bien la carga se distribuyó mejor, Java seguía teniendo un rol predominante, lo cual motivó la necesidad de evaluar una configuración aún más exigente.


### Pool con Configuración Muy Agresiva

![Diagrama Tiempo de Transacción Web con un Pool de Tamaño Máximo 80](Diagramas/80maxPool.png)

*Figura 4. Visualización del tiempo de transacción web obtenida desde New Relic bajo una configuración muy agresiva de HikariCP, con un maximumPoolSize de 80 y un minimumIdle de 40.*

Esta configuración se probó con el objetivo de observar si el aumento de conexiones permitía una distribución más balanceada de la carga entre el backend y la base de datos. A diferencia de las pruebas anteriores, se buscó identificar un punto donde MySQL empezara a tener un rol más dominante, reduciendo la presión sobre Java.

---

## 4. Conclusión



---

## 5. Referencias


