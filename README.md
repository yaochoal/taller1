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
1. Abrir el ejecutable Programa/Taller.jar
2. Seleccionar la plantilla_ejemplo.txt
3. Seleccionar la tabla_ejemplo.xlsx
4. Seleccionar una carpeta donde guardar los documentos generados.
5. Oprimir en Iniciar Programa.


## Desarrollo

### 1. Uso de JFlex:
Se utililizo JFlex para el reconocimiento de los campos de la plantilla y a partir del Token definido Campo con la estructura << Campo >> por ejemplo << Nombre >> una vez almacenado en un Array de Campos se busca el campo en el excel.
### 2. Busqueda en el Excel:
Se utilizo la libreria Apache POI para abrir un documento de Excel.xlsx del cual se busca en cada uno de las celdas los Campos identificados por el Analizador Lexico y sus respectivos datos.
### 3. Generación de documentos:
Se utilizo las funciones nativas de Java para a partir de la plantilla original crear mas documentos de formato Texto.txt reemplazando los Campos con los respectivos datos encontrados en el Excel.
### 3. Reporte de errores en plantilla:
Se hace un control de los Campos encontrados en el Excel con una variable boleana la cual detecta si se encontraron todos los campos en el Excel donde de lo contrario reporta al usuario de este hecho y procede a buscar el Campo mas similar calculando la distancia de Levenshtein y dando opción al usuario de elegir si cambiar o no el Campo no encontrado por el encontrado.