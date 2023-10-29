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
  $ export CLASSPATH=".:./lib/*"
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
Los resultados escritos en el informe son replicables (obviando las variaciones por inicializaciones aleatorias) mediante la ejecución de los drivers implementados para cada experimento y el correspondiente script de R para interpretar los resultados.

### Experimento 1: Selección del conjunto de operadores
Para obtener los boxplots y t-tests apropiados ejecutar


### Experimento 2: Selección de la estrategia de generación de la solución inicial

### Experimento 3: Selección de los parámetros para Simulated Annealing

### Experimento 4: Estudio de la evolución del tiempo de ejecución en función del tamaño del problema

### Experimento 5: Comparación de los dos algoritmos de búsqueda para heurísticas diferentes

### Experimento 6: Estudio del comportamiento del algoritmo para escenarios de hora punta

### Experimento 7: Estimación del número de furgonetas óptimo

