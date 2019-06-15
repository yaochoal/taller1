
# Taller #01 ANÁLISIS LEXICO

## Propósito

Análisis, diseño e implementación de una herramienta (prototipo) de software que permita realizar combinación de correspondencia a partir de un archivo fuente de información y una plantilla o modelo de documento a generar. El archivo fuente es una tabla (por ejemplo, Excel) cuyas columnas son los campos.

## Integrantes

|       Integrante      |                 Correo                       |
|-----------------------|-----------------------------------------------|
| Adriano Ramón Hernández|  <aramonh@unal.edu.co> |
| Liseth Yurany Arévalo Yaruro   |   <lyarevalo@unal.edu.co>  |
| Luis Alejandro Higuarán Serrano      |    <lahiguarans@unal.edu.co>    |
| Michael Tomás Velásquez Gordillo      |   <mtvelasquezg@unal.edu.co>     |
| Yesid Alberto Ochoa Luque      |    <yaochoal@unal.edu.co>     |

## Requisitos
Java 1.8 o superior.

## Instrucciones de ejecución
### Primer escenario: Sin campos a corregir en Excel
1. Abrir el ejecutable Programa/Taller.jar
2. Seleccionar la plantilla_ejemplo.txt
3. Seleccionar la tabla_ejemplo.xlsx
4. Seleccionar una carpeta donde guardar los documentos generados.
5. Oprimir en Iniciar Programa.
### Segundo escenario: Con campos a corregir en Excel
1. Abrir el ejecutable Programa/Taller.jar
2. Seleccionar la plantilla_ejemplo.txt
3. Seleccionar la tabla_ejemplo1.xlsx
4. Seleccionar una carpeta donde guardar los documentos generados.
5. Oprimir en Iniciar Programa.
6. Cuando aparezca que si desea reemplazar el campo por el el que se le asemeja oprimir que si.

## Diseño

### 1. Uso de JFlex:
Se utiliza  JFlex para el reconocimiento de los campos de la plantilla y a partir del Token definido Campo con la estructura << Campo >> por ejemplo << Nombre >>, una vez almacenados todos los Campos en un Array se busca el respectivo campo en el Excel.
### 2. Búsqueda  en el Excel:
Se utiliza la librería Apache POI para abrir un documento de Excel.xlsx del cual se busca en cada una de las celdas los Campos guardados en el Array de campos, marcando con una variable booleana el Campo encontrado y guardando el indice de su fila y columna en un Array de Columnas.
### 3. Generación de documentos:
Se utiliza las funciones nativas de Java para a partir de la plantilla original crear mas documentos de formato Texto.txt reemplazando los Campos con los respectivos datos que están justo una fila debajo del titulo del Campo uno por uno hasta recorrer todos los datos.
### 3. Reporte de errores en plantilla:
Se hace un control de los Campos encontrados en el Excel con una variable booleana la cual detecta si se encontraron todos los campos en el Excel donde de lo contrario reporta al usuario de este hecho y procede a buscar el Campo más similar calculando la distancia de Levenshtein con un método y dando opción al usuario de elegir si cambiar o no el Campo por uno similar a este.

## Análisis de resultados.
La creación de esta herramienta facilita mucho la creación de documentos que tiene un formato predeterminado o plantilla para generar grandes cantidades de archivos y enviarlos de ser necesario a sus destinatarios o solo almacenarlos, gracias al uso de Flex o JFlex da herramientas muy simples e intuitivas  para encontrar los campos los cuales son definidos por un patrón de caracteres << Campo >> de manera muy eficiente de la plantilla donde se podrían definir muchos mas Tokens en el archivo y ampliar el alcance de la herramienta definiendo otros comportamientos que ejecuten más funcionalidades.