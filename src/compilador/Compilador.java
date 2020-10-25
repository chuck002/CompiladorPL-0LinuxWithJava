/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;

public class Compilador {

    public static void main(String[] args) {
        AnalizadorLexico aLex;
        IndicadorDeErrores indicadorDeErrores = null;
        Reader archFuente;
        AnalizadorSemantico aSem;
        GeneradorDeCodigo generadorDeCodigo;

        try {
            indicadorDeErrores = new IndicadorDeErrores();
            String nomArch = "";
            JFileChooser fc = new JFileChooser("~\\");
            if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                nomArch = fc.getSelectedFile().getPath();
            }

            archFuente = new BufferedReader(new FileReader(nomArch));
            aLex = new AnalizadorLexico(archFuente);

            aSem = new AnalizadorSemantico(indicadorDeErrores);
            generadorDeCodigo = new GeneradorDeCodigo(indicadorDeErrores, nomArch);
            
            
            
            AnalizadorSintactico aSint = new AnalizadorSintactico(aLex, aSem, indicadorDeErrores, generadorDeCodigo);

            aSint.analizar();

            indicadorDeErrores.mostrar(0);

        } catch (FileNotFoundException ex) {
            indicadorDeErrores.mostrar(1, ex.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Compilador.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
