
# Práctica de Búsqueda Local
Implementación de los scripts para implementar la práctica de Inteligéncia Artificial (GEI IA 2023-2024 Q1) y realizar experimentos.




## Autores

- Luca Acosta Iglesias
- Sergi Martínez Pamias
- Guillermo Medina Duran
- Albert Puigvert Lorente
## Ejecución

Antes de ejecutar cualquier experimento hace falta compilar el código de las clases java.

```bash
  $ cd <Path_carpeta_base>
  $ export CLASSPATH=".:./lib/*"    // Necesario para compilar y ejecutar
  $ javac *.java
```
A partir de ahora consideramos que todos los comandos se ejecutan des del directorio base.

El código principal de la práctica se encuentra en `Main.java` y en el package `IA/BicingBusquedaLocal`, para ejecutar pruebas con los parámetros que se quieran probar:

```bash
  $ java Main         // Interfaz por consola limitada
  $ java Main --help  // Listado de opciones y flags para personalizar la ejecución
```

Para cada experimento se ha implementado un driver que se acopla a `Main.java` y escribe en un fichero de texto el resultado de la ejecución (En cada experimento se pide el nombre base para después importar los datos con R).

La ejecución de un experimento se puede realizar ejecutando:

```bash
  $ java <Nombre_experimento>
```





## Experimentos
Los resultados escritos en el informe son replicables (obviando las variaciones por inicializaciones aleatorias) mediante la ejecución de los drivers implementados para cada experimento y el correspondiente script de *R* para interpretar los resultados.

Para que *R* detecte los archivos es necesario cambiar el nombre de la variable `base` en cada script al path donde estan los archivos de texto generados más el nombre base introducido. Se recomienda usar *RStudio* para visualizar los gráficos generados.

### Experimento 1: Selección del conjunto de operadores
Para obtener los boxplots y t-tests usados en el apartado 6.1 ejecutar `$ java Experimento1`, cambiar en `Experimento1.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*. 

### Experimento 2: Selección de la estrategia de generación de la solución inicial
Para obtener los boxplots y t-tests usados en el apartado 6.2 ejecutar `$ java Experimento2`, cambiar en `Experimento2.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*. 

### Experimento 3: Selección de los parámetros para Simulated Annealing
Para este experimento se han usado varios drivers y scripts para los diferentes gráficos.

#### Grafico individual beneficio~iteración
Ejecutar  `$ java Experimento3_1`, cambiar en `Experimento3_1.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*.

#### Tabla de gráficos beneficio~iteración para combinaciones de múltiples valores de k y lambda
Ejecutar  `$ java Experimento3_1multi`, cambiar en `Experimento3_1multi.R` el valor de la variable `base` al apropiado, rellenar los vectores `kVals` y `lambdaVals` con los valores de *k* y *lambda* usados (en el mismo orden en que se han especificado) y ejecutar el script de *R*.

#### Tabla de medias del beneficio obtenido
Ejecutar `$ java Experimento3_2PAR` (si la ejecución paralela da problemas se puede ejecutar la versión secuencial haciendo `$ java Experimento3_2`), cambiar en `Experimento3_2.R` el valor de la variable `base` al apropiado, rellenar los vectores `kVals` y `lambdaVals` con los valores de *k* y *lambda* usados (en el mismo orden en que se han especificado) y ejecutar el script de *R*.

Cada columna de las tablas obtenidas representa un valor de *k* y cada fila un valor de *lambda*. Las dos tablas generadas son:
- **means**: Media de los beneficios obtenidos para cada combinación.
- **diffMeans**: Media de la diferencia `beneficio(Simulated Annealing) - beneficio(Hill Climbing)`.

### Experimento 4: Estudio de la evolución del tiempo de ejecución en función del tamaño del problema
Ejecutar `$ java Experimento4`.

Para obtener un gráfico sencillo del tiempo de ejecución en función del número de estaciones cambiar en `Experimento4.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*.

Para obtener el gráfico con la línea de tendencia se ha usado una hoja de cálculo de google spreadsheets y la función integrada para encontrar líneas de tendencia.

### Experimento 5: Comparación de los dos algoritmos de búsqueda para heurísticas diferentes
Para obtener los boxplots y t-tests usados en el apartado 6.5 ejecutar `$ java Experimento5`, cambiar en `Experimento5.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*.

### Experimento 6: Estudio del comportamiento del algoritmo para escenarios de hora punta
Para obtener el boxplot y t-test usado en el apartado 6.6 ejecutar `$ java Experimento6`, cambiar en `Experimento6.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*.

### Experimento 7: Estimación del número de furgonetas óptimo
Para obtener los 2 boxplot y los 2 t-test usados en el apartado 6.7 ejecutar `$ java Experimento7`, cambiar en `Experimento7.R` el valor de la variable `base` al apropiado y ejecutar el script de *R*.
