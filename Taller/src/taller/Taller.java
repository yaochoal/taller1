/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class Taller {
    public static ArrayList<String> Campos = new ArrayList<String>(); 
    public static String[][] Matriz = new String[100][100];
    
    public static void gemerarLexer(){
        String ruta = "src/taller/Lexer.flex";
        File archivo = new File(ruta);
        jflex.Main.generate(archivo);
    }
    
    public static void generarMatriz() throws FileNotFoundException, IOException{
        File file = new File("src/taller/Tabla.xlsx"); 
         FileInputStream fip = new FileInputStream(file); 
         XSSFWorkbook libro = new XSSFWorkbook(fip); 
         XSSFSheet hoja = libro.getSheetAt(0);
         Iterator <Row> filas = hoja.iterator();
         Iterator <Cell> celdas;
         Row fila;
         Cell celda;
         while(filas.hasNext()){
             fila = filas.next();
             celdas = fila.cellIterator();
             while (celdas.hasNext()) {                 
                 celda=celdas.next();
                 switch(celda.getCellType()){ 
                     case _NONE:
                         break;
                     case NUMERIC:
                         Double numero = celda.getNumericCellValue();
                         Matriz[celda.getColumnIndex()][celda.getRowIndex()] =  numero.toString();
                         break;
                     case STRING:
                         Matriz[celda.getColumnIndex()][celda.getRowIndex()] = celda.getStringCellValue();
                         break;
                     case FORMULA:
                         Matriz[celda.getColumnIndex()][celda.getRowIndex()] = celda.getCellFormula();
                         break;
                     case BLANK:
                         break;
                     case BOOLEAN:
                         break;
                     case ERROR:
                         break;
                     default:
                         throw new AssertionError(celda.getCellType().name());         
             }
             }
         }
         libro.close();
    }
    public static void buscarCamposTabla() throws FileNotFoundException, IOException{
        generarMatriz();
        Boolean Coinciden = true;
        for (int i = 0; i < Campos.size(); i++) {
            if(Campos.get(i).equalsIgnoreCase(Matriz[i][0])){
                System.out.println("Coinciden");
            }
            
        }
    }
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String rutaarchivo = "src/taller/plantilla.txt";
        gemerarLexer();
        FileReader fileReader = new FileReader(rutaarchivo);
        Reader lector = new BufferedReader( new FileReader(rutaarchivo));
        StringBuilder sb = new StringBuilder();
        Lexer lexer = new Lexer(lector);
        String resultado = "";
        while(true){
                Tokens tokens = lexer.yylex();
                if (tokens == null) {
                    //resultado += "FIN";
                    buscarCamposTabla();
                    System.out.println(resultado);
                    return;
                }
                switch(tokens){
                    case Reservadas: case Campo:
                        //System.out.println(lexer.lexeme);
                        String campo = lexer.lexeme.replaceAll("<|>", "");
                        Campos.add(campo);
                        resultado += "Campo: "+ lexer.lexeme+ " Encontrado\n";
                        //resultado += lexer.lexeme +": Es un "+ tokens + "\n";
                        break;
                    default:
                        //resultado += "Token: " +tokens + "\n";
                        break;
                }
            }
        
    }
    
    public static int Levenshtein(String PalabraA, String PalabraB){
        
        int size_x = PalabraA.length() + 1;
        int size_y = PalabraB.length() + 1;
        int[][] distances = new int[size_x][size_y];
        
        for (int i = 0; i < size_x; i++) {  
            distances[i][0] = i;
        }                                                                              
        for (int j = 0; j < size_y; j++) {  
            distances[0][j] = j; 
        }
        
        for (int x = 1; x < size_x; x++) {
            for (int y = 1; y < size_y; y++) {
                if (PalabraA.charAt(x-1) == PalabraB.charAt(y-1)) {    
                    distances[x][y] = Math.min((Math.min(distances[x-1][y] + 1, distances[x-1][y-1])), distances[x][y-1] + 1);
                } else {  
                    distances[x][y] = Math.min((Math.min(distances[x-1][y] + 1, distances[x-1][y-1] + 1)), distances[x][y-1] + 1);
                }
            }
        }
        
        return (distances[size_x - 1][size_y - 1]);        
    }
    
}
