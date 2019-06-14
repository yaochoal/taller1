/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



public class Taller {
    public static int tamaño = 1000;
    public static ArrayList<Campo> Campos = new ArrayList<>(); 
    public static ArrayList<Columna> Columnas = new ArrayList<>();
    public static String[][] Matriz = new String[tamaño][tamaño];
    
    public static void generarLexer(){
        String ruta = "src/taller/Lexer.flex";
        File archivo = new File(ruta);
        jflex.Main.generate(archivo);
    }
    
    public static void pasarExcelAMatriz() throws FileNotFoundException, IOException{
        File file = new File("src/input_file/Tabla.xlsx"); 
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
    
    public static void buscarCamposEnTabla() throws FileNotFoundException, IOException{
        for (int i = 0; i < Campos.size(); i++) {
            for (int j = 0; j < tamaño; j++) {
                for (int k = 0; k < tamaño; k++) {
                    if(Campos.get(i).Nombre.equalsIgnoreCase(Matriz[j][k])){
                       //System.out.println("Coinciden: "+ Campos.get(i)+" y "+ Matriz[j][k]);
                       //System.out.println("Campo: "+i+" Indice: "+ j+" "+k); 
                        Campos.get(i).Encontrado = true;
                        Columna columna = new Columna(Campos.get(i).Nombre,j,k);
                        Columnas.add(columna);
                    }
                    //System.out.println(Levenshtein(Campos.get(i),Matriz[j][k]));;
                }
            }
        }
    }
    
    public static boolean verificarCampos(int j){
        boolean campos =true;
        for (Columna Columna : Columnas) {
            campos = campos && (Matriz[Columna.columna][Columna.fila + j] != null);      
        }
        return campos;
    }
    
    public static void generarMails() throws IOException{
       for (int i = 1; verificarCampos(i) ; i++) {
       File original = new File("src/input_file/plantilla.txt");
       File copiado = new File("src/output_file/"+i+".txt");
       FileWriter archivoEscribir = new FileWriter(copiado);
       FileReader archivoLeer = new FileReader(original);
       BufferedWriter bufferEscribir =  new BufferedWriter(archivoEscribir);
       BufferedReader bufferLeer = new BufferedReader(archivoLeer);
       String line = null;
       while((line = bufferLeer.readLine()) != null) {
                line=line.replaceAll("<|>", "");
                for (int j = 0; j < Columnas.size(); j++) {
                    line=line.replaceAll(Columnas.get(j).nombre, Matriz[Columnas.get(j).columna][Columnas.get(j).fila+i]);
                }
                //System.out.println(line);
                bufferEscribir.write(line+"\n");
       }   
       
        bufferEscribir.close();
        bufferLeer.close();
       }
       System.out.println("Documentos generados exitosamente");
    }
    
    public static void iniciarPrograma() throws FileNotFoundException, IOException{
        String rutaarchivo = "src/input_file/plantilla.txt";
        //generarLexer();
        FileReader fileReader = new FileReader(rutaarchivo);
        Reader lector = new BufferedReader( new FileReader(rutaarchivo));
        StringBuilder sb = new StringBuilder();
        Lexer lexer = new Lexer(lector);
        String resultado = "";
        while(true){
                Tokens tokens = lexer.yylex();
                if (tokens == null) {
                    //resultado += "FIN";
                    pasarExcelAMatriz();
                    buscarCamposEnTabla();
                    buscarCamposFaltantes();
                    generarMails();
                    //System.out.println(resultado);
                    return;
                }
                switch(tokens){
                    case Reservadas: case Campo:
                        //System.out.println(lexer.lexeme);
                        String campo = lexer.lexeme.replaceAll("<|>", "");
                        Campo campoObj = new Campo(campo);
                        Campos.add(campoObj);
                        //resultado += "Campo: "+ lexer.lexeme+ " Encontrado\n";
                        //resultado += lexer.lexeme +": Es un "+ tokens + "\n";
                        break;
                    default:
                        //resultado += "Token: " +tokens + "\n";
                        break;
                }
            }
    }
    
    public static void buscarCamposFaltantes(){
        for (int i = 0; i < Campos.size(); i++) {
            if(Campos.get(i).Encontrado==false){
                System.out.println("Campo: "+Campos.get(i).Nombre+" No encontrado");
                 int Nearest = 100;
                 String PalabraA  = Campos.get(i).Nombre.toUpperCase();
                 String PalabraB;
                 String Candidata = "";
                 int IndiceJ = 0;
                 int IndiceK = 0;
                 for (int j = 0; j < tamaño; j++) {
                     for (int k = 0; k < tamaño; k++) {
                        if(Matriz[j][k]!=null){
                        PalabraB = Matriz[j][k].toUpperCase();
                        int distancia = Levenshtein(PalabraA, PalabraB);
                            if (distancia < Nearest) {
                                Nearest = distancia;
                                Candidata = Matriz[j][k];
                                IndiceJ= j;
                                IndiceK= k;
                            }
                        }
                     }
                }
                System.out.println("El campo candidato para el token en la plantilla: <<" + Campos.get(i).Nombre + ">> del excel es: " + Candidata);  
                System.out.println("¿Desea que este reemplaze al campo faltante?\nDigite: Si/No"); 
                Scanner in = new Scanner(System.in);
                String s = in.nextLine();
                if(s.equalsIgnoreCase("Si")){
                    Matriz[IndiceJ][IndiceK]=Campos.get(i).Nombre;
                    Columna columna = new Columna(Campos.get(i).Nombre,IndiceJ,IndiceK);
                    Columnas.add(columna);
                    System.out.println("Campo Reemplazado");
                }
            }
        }
    }
    public static int Levenshtein(String PalabraA, String PalabraB){
        if(PalabraA==null){
           PalabraA="";
        }
        if(PalabraB==null){
           PalabraB="";
        }
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
    
    
    public static void main(String[] args) throws IOException{
        iniciarPrograma();
    }
    
}
