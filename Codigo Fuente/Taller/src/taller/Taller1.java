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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class Taller1 extends javax.swing.JFrame {

    
    public Taller1() {
        initComponents();
        this.setTitle("Taller 1: Generador de documentos");
        this.setResizable(false);
    }
    //Se declaran las variables globales
    public static int tamaño;
    public static ArrayList<Campo> Campos; 
    public static ArrayList<Columna> Columnas;
    public static String[][] Matriz;
    public static String result = "";
    //Metodo para regenerar el Lexer.java en caso de ser necesario
    public static void generarLexer(){
        String ruta = "src/taller/Lexer.flex";
        File archivo = new File(ruta);
        jflex.Main.generate(archivo);
    }
    //Funcion que ejecuta el programa y el lexer para encontrar los Campos
    //En la plantilla y luego buscarlos en el Excel
    public static void iniciarPrograma() throws FileNotFoundException, IOException{
        tamaño = 1000;
        Campos = new ArrayList<>();
        Columnas = new ArrayList<>();
        Matriz = new String[tamaño][tamaño];
        //generarLexer();
        FileReader fileReader = new FileReader(plantillaDireccion.getText());
        Reader lector = new BufferedReader( new FileReader(plantillaDireccion.getText()));
        StringBuilder sb = new StringBuilder();
        //Se llama a la clase Lexer que ejecuta
        Lexer lexer = new Lexer(lector);
        while(true){
                Tokens tokens = lexer.yylex();
                if (tokens == null) {
                    //Cuando encuentra los campos y los guarda
                    //Ejecuta el resto de metodos
                    pasarExcelAMatriz();
                    buscarCamposEnTabla();
                    buscarCamposFaltantes();
                    generarMails();
                    return;
                }
                switch(tokens){
                    case Reservadas: case Campo:
                        String campo = lexer.lexeme.replaceAll("<|>", "");
                        Campo campoObj = new Campo(campo);
                        Campos.add(campoObj);
                        break;
                    default:
                        break;
                }
            }
    }
    //Metodo para pasar todas las celdas del excel a una Matriz de Strings
    public static void pasarExcelAMatriz() throws FileNotFoundException, IOException{
        File file = new File(excelDireccion.getText()); 
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
    //Metodo que busca los Campos de la plantilla en la Tabla
    public static void buscarCamposEnTabla() throws FileNotFoundException, IOException{
        for (int i = 0; i < Campos.size(); i++) {
            for (int j = 0; j < tamaño; j++) {
                for (int k = 0; k < tamaño; k++) {
                    if(Campos.get(i).Nombre.equalsIgnoreCase(Matriz[j][k])){
                        Campos.get(i).Encontrado = true;
                        Columna columna = new Columna(Campos.get(i).Nombre,j,k);
                        Columnas.add(columna);
                    }
                }
            }
        }
    }
    //Metodo que verifica que todos los campos tengan la misma cantidad de datos
    //en las filas.
    public static boolean verificarCampos(int j){
        boolean campos =true;
        for (Columna Columna : Columnas) {
            campos = campos && (Matriz[Columna.columna][Columna.fila + j] != null);      
        }
        return campos;
    }
    //Metodo que genera los archivos txt a enviar
    //Y reemplaza los campos por el respectivo valor en la tabla 
    public static void generarMails() throws IOException{
       for (int i = 1; verificarCampos(i) ; i++) {
       File original = new File(plantillaDireccion.getText());
       File copiado = new File(guardarDireccion.getText()+"/"+i+".txt");
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
                bufferEscribir.write(line+"\n");
       }   
        bufferEscribir.close();
        bufferLeer.close();
       }
       JFrame frame = new JFrame();
       if(result.equals("")){
           result = "Documentos generados exitosamente";
       }
       JOptionPane.showMessageDialog(frame,result);
    }
    //Metodo que detecta si faltaron campos en el Excel
    //y ejecuta la funcion levenshtein para detectar uno similar
    //y su el usuario acepta reemplazarlo por ese campo
    public static void buscarCamposFaltantes(){
        for (int i = 0; i < Campos.size(); i++) {
            if(Campos.get(i).Encontrado==false){
                //System.out.println("Campo: "+Campos.get(i).Nombre+" No encontrado");
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,"Campo: <<"+Campos.get(i).Nombre+">> no encontrado en el Excel");
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
                int option = JOptionPane.showConfirmDialog(null, "El campo candidato para el token <<" + Campos.get(i).Nombre + ">> del excel es: " + Candidata
                        +"\n¿Desea que este reemplaze al campo faltante?", "Mensaje", 
                                JOptionPane.YES_NO_OPTION);
                System.out.println(option);
                if(option == 0){
                    Matriz[IndiceJ][IndiceK]=Campos.get(i).Nombre;
                    Columna columna = new Columna(Campos.get(i).Nombre,IndiceJ,IndiceK);
                    Columnas.add(columna);
                }else{
                    result = "Documentos generados con campos Faltantes.";
                }
            }
        }
    }
    //Metodo para medir la distancia entre 2 Strings
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
    
    //Metodos de la interfaz Grafica
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tituloPlantilla = new javax.swing.JLabel();
        plantillaDireccion = new javax.swing.JTextField();
        tituloExcel = new javax.swing.JLabel();
        excelDireccion = new javax.swing.JTextField();
        tituloGuardar = new javax.swing.JLabel();
        guardarDireccion = new javax.swing.JTextField();
        botonIniciarPrograma = new javax.swing.JButton();
        plantillaBoton = new javax.swing.JButton();
        excelBoton = new javax.swing.JButton();
        directorioBoton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        tituloPlantilla.setText("Dirección de Plantilla:");

        plantillaDireccion.setEditable(false);
        plantillaDireccion.setText("src/input_file/plantilla.txt");
        plantillaDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plantillaDireccionActionPerformed(evt);
            }
        });

        tituloExcel.setText("Dirección de Excel:");

        excelDireccion.setEditable(false);
        excelDireccion.setText("src/input_file/Tabla.xlsx");

        tituloGuardar.setText("Dirección de ficheros a Guardar:");

        guardarDireccion.setEditable(false);
        guardarDireccion.setText("src/output_file/");
        guardarDireccion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarDireccionActionPerformed(evt);
            }
        });

        botonIniciarPrograma.setText("Iniciar Programa");
        botonIniciarPrograma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                botonIniciarProgramaActionPerformed(evt);
            }
        });

        plantillaBoton.setText("Buscar Plantilla");
        plantillaBoton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                plantillaBotonActionPerformed(evt);
            }
        });

        excelBoton.setText("Buscar Excel");
        excelBoton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                excelBotonActionPerformed(evt);
            }
        });

        directorioBoton.setText("Buscar Directorio");
        directorioBoton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directorioBotonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(directorioBoton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(botonIniciarPrograma))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(plantillaDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 408, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tituloPlantilla)
                                .addComponent(tituloExcel)
                                .addComponent(excelDireccion)
                                .addComponent(guardarDireccion, javax.swing.GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
                                .addComponent(excelBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tituloGuardar)
                            .addComponent(plantillaBoton, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(tituloPlantilla)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(plantillaDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(plantillaBoton)
                .addGap(2, 2, 2)
                .addComponent(tituloExcel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(excelDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(excelBoton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tituloGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guardarDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(botonIniciarPrograma))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(directorioBoton)))
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void plantillaDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plantillaDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_plantillaDireccionActionPerformed

    private void guardarDireccionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarDireccionActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_guardarDireccionActionPerformed
    //Metodo que se acciona al oprimir el boton Ejecutar
    private void botonIniciarProgramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_botonIniciarProgramaActionPerformed
        if(plantillaDireccion.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Selecciona una plantilla tipo Texto");
        }else{
            if(excelDireccion.getText().equals("")){
            JOptionPane.showMessageDialog(null,"Selecciona un documento de Excel");
             }else{
                if(guardarDireccion.getText().equals("")){
                    JOptionPane.showMessageDialog(null,"Selecciona una carpeta para Guardar los documentos");
                }else{
                    try {
                        iniciarPrograma();
                    } catch (IOException ex) {
                        Logger.getLogger(Taller1.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        
       
        
    }//GEN-LAST:event_botonIniciarProgramaActionPerformed
    //Metodo que se acciona al oprimir el boton Buscar Plantilla
    private void plantillaBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_plantillaBotonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(plantillaBoton.getText());
        FileFilter filter = new FileNameExtensionFilter("Archivo de Texto", "txt");
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(this);
        File f = chooser.getSelectedFile();
        plantillaDireccion.setText(f.getAbsolutePath());
    }//GEN-LAST:event_plantillaBotonActionPerformed
    //Metodo que se acciona al oprimir el boton Buscar Excel
    private void excelBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_excelBotonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(excelBoton.getText());
        FileFilter filter = new FileNameExtensionFilter("Archivo de Excel", "xls", "xlsx");
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(this);
        File f = chooser.getSelectedFile();
        excelDireccion.setText(f.getAbsolutePath());
    }//GEN-LAST:event_excelBotonActionPerformed
    //Metodo que se acciona al oprimir el boton Buscar Directorio
    private void directorioBotonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directorioBotonActionPerformed
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(directorioBoton.getText());
        chooser.showOpenDialog(this);
        guardarDireccion.setText(chooser.getSelectedFile().getAbsolutePath());
    }//GEN-LAST:event_directorioBotonActionPerformed

    //Main con ejecución de la interfaz grafica
    public static void main(String args[]) {
       
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Taller1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Taller1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Taller1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Taller1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Taller1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton botonIniciarPrograma;
    private javax.swing.JButton directorioBoton;
    private javax.swing.JButton excelBoton;
    public static javax.swing.JTextField excelDireccion;
    public static javax.swing.JTextField guardarDireccion;
    private javax.swing.JButton plantillaBoton;
    public static javax.swing.JTextField plantillaDireccion;
    private javax.swing.JLabel tituloExcel;
    private javax.swing.JLabel tituloGuardar;
    private javax.swing.JLabel tituloPlantilla;
    // End of variables declaration//GEN-END:variables
}
