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


