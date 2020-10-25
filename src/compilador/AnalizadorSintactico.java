/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador;

import java.io.IOException;

/**
 *
 * @author javy
 */
public class AnalizadorSintactico {

    private AnalizadorLexico aLex;
    private AnalizadorSemantico aSem;
    private IndicadorDeErrores indicadorDeErrores;
    private GeneradorDeCodigo generadorDeCodigo;
    private int valorVar;

    AnalizadorSintactico(AnalizadorLexico aLex, AnalizadorSemantico aSem, IndicadorDeErrores indicadorDeErrores, GeneradorDeCodigo generadorDeCodigo) {
        this.aLex = aLex;
        this.aSem = aSem;
        this.indicadorDeErrores = indicadorDeErrores;
        this.generadorDeCodigo = generadorDeCodigo;
        this.valorVar = 0;

    }

    public void analizar() throws IOException {

        aLex.escanear();
        programa();

    }

    private void programa() throws IOException {
        // generadorDeCodigo.cargarBloqueFijoWindows(); // BLOQUE FIJO WINDOWS
        generadorDeCodigo.cargarBloqueFijoLinux();
        generadorDeCodigo.cargarByte(0xBF);//Mov Edi,...
        generadorDeCodigo.cargarInt(0);
        int posEdi = generadorDeCodigo.getTopeMemoria();
        bloque(0);

        if (aLex.getS() == Terminal.PUNTO) {
            aLex.escanear();
        } else {
            indicadorDeErrores.mostrar(2, aLex.getCad(), aLex.getFila(), aLex.getColumna());
        }
        int dis = 0x300 - (generadorDeCodigo.getTopeMemoria() + 5); // 
        generadorDeCodigo.cargarByte(0xE9); // JMP 0x0588 Sale del programa WINDOWS y 0x300 para LINUX
        generadorDeCodigo.cargarInt(dis); // 

        /* *** WINDOWS ***
        int posVar = generadorDeCodigo.leerIntEn(212)//image base
                + generadorDeCodigo.leerIntEn(204)//base code
                + generadorDeCodigo.getTopeMemoria()
               - Constantes.TAMANIOENCABEZADOWINDOWS;
        *** WINDOWS ***
         */
        //Linux
        int posVar = generadorDeCodigo.leerIntEn(193) + generadorDeCodigo.getTopeMemoria() - Constantes.TAMANIOENCABEZADOLINUX;
        //int posVar = 134512864;
        System.out.printf("%d ---- %x\n", posVar, posVar);
        generadorDeCodigo.cargarIntEn(posVar, posEdi - 4);

        int cantVar = valorVar / 4;
        for (int i = 0; i < cantVar; i++) {
            generadorDeCodigo.cargarInt(0);
        }

        /*
        int tamSeccionText = generadorDeCodigo.getTopeMemoria() - Constantes.TAMANIOENCABEZADOWINDOWS;
        generadorDeCodigo.cargarIntEn(tamSeccionText, 416);//VirtualSize
        int fileAlignment = generadorDeCodigo.leerIntEn(220);
        while (generadorDeCodigo.getTopeMemoria() % fileAlignment != 0) {
            generadorDeCodigo.cargarByte(0);
        }

        tamSeccionText = generadorDeCodigo.getTopeMemoria() - Constantes.TAMANIOENCABEZADOWINDOWS;
        generadorDeCodigo.cargarIntEn(tamSeccionText, 188);// SizeOfCodeSection
        generadorDeCodigo.cargarIntEn(tamSeccionText, 424);// SizeOfRawData

        int sectionAlignment = generadorDeCodigo.leerIntEn(216);
        generadorDeCodigo.cargarIntEn((2 + tamSeccionText / sectionAlignment) * sectionAlignment, 240);//SizeOfImage 
        generadorDeCodigo.cargarIntEn((2 + tamSeccionText / sectionAlignment) * sectionAlignment, 208);//BaseOfData
        generadorDeCodigo.volcar();
        
        **** WINDOWS ****
         */
        int tamSeccionText = generadorDeCodigo.getTopeMemoria() - Constantes.TAMANIOENCABEZADOLINUX;
        generadorDeCodigo.cargarIntEn(tamSeccionText, 68); // FileZise
        generadorDeCodigo.cargarIntEn(tamSeccionText, 72); // MemorySize

        tamSeccionText = generadorDeCodigo.getTopeMemoria() - Constantes.TAMANIOENCABEZADOLINUX;
        generadorDeCodigo.cargarIntEn(tamSeccionText, 201); // Size 
        generadorDeCodigo.volcar();
    }

    private void bloque(int base) throws IOException {

        int desplazamiento = 0;
        generadorDeCodigo.cargarByte(0xE9);
        generadorDeCodigo.cargarInt(0);
        int iniSalto = generadorDeCodigo.getTopeMemoria();
        /*
        if (aLex.getS() == Terminal.CONST) {
            aLex.escanear();
            if (aLex.getS() == Terminal.IDENTIFICADOR) {
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                if (i == null) {
                    aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.CONST, 0);
                    desplazamiento++;
                } else {
                    indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            if (aLex.getS() == Terminal.IGUAL) {
                aLex.escanear();

            } else {
                indicadorDeErrores.mostrar(5, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            if (aLex.getS() == Terminal.NUMERO) {
                try {
                    aSem.agregarValor(base + desplazamiento - 1, Integer.parseInt(aLex.getCad()));
                } catch (ArithmeticException ex) {
                    indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }

            while (aLex.getS() == Terminal.COMA) {
                aLex.escanear();
                if (aLex.getS() == Terminal.IDENTIFICADOR) {
                    IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                    if (i == null) {
                        aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.CONST, 0);
                        desplazamiento++;
                    } else {
                        indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                if (aLex.getS() == Terminal.IGUAL) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(5, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                if (aLex.getS() == Terminal.MENOS) {
                    aLex.escanear();
                    if (aLex.getS() == Terminal.NUMERO) {
                        try {
                            aSem.agregarValor(base + desplazamiento - 1, -1 * Integer.parseInt(aLex.getCad()));
                        } catch (ArithmeticException ex) {
                            indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                        }
                        aLex.escanear();
                    } else {
                        indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                } else if (aLex.getS() == Terminal.NUMERO) {
                    try {
                        aSem.agregarValor(base + desplazamiento - 1, Integer.parseInt(aLex.getCad()));
                    } catch (ArithmeticException ex) {
                        indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
            }
            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
        }

        if (aLex.getS() == Terminal.VAR) {
            aLex.escanear();
            if (aLex.getS() == Terminal.IDENTIFICADOR) {
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                if (i == null) {
                    aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.VAR, valorVar);
                    valorVar = valorVar + 4;
                    desplazamiento++;
                } else {
                    indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }

                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            while (aLex.getS() == Terminal.COMA) {
                aLex.escanear();
                if (aLex.getS() == Terminal.IDENTIFICADOR) {
                    IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                    if (i == null) {
                        aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.VAR, valorVar);
                        valorVar = valorVar + 4;
                        desplazamiento++;
                    } else {
                        indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }

                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
            }
            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
        }

        while (aLex.getS() == Terminal.PROCEDURE) {
            aLex.escanear();
            if (aLex.getS() == Terminal.IDENTIFICADOR) {
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                if (i == null) {
                    aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.PROCEDURE, generadorDeCodigo.getTopeMemoria());
                    desplazamiento++;
                } else {
                    indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }

                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            bloque(base + desplazamiento);
            generadorDeCodigo.cargarByte(0xC3);//RET

            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
        }
        int dis = generadorDeCodigo.getTopeMemoria() - iniSalto;
        if (dis != 0) {
            generadorDeCodigo.cargarIntEn(dis, iniSalto - 4);
        } else {
            generadorDeCodigo.setTopeMemoria(generadorDeCodigo.getTopeMemoria() - 5);
        }
        proposicion(base, desplazamiento);
    }
         */
        if (aLex.getS() == Terminal.CONST) {
            aLex.escanear();
            if (aLex.getS() == Terminal.IDENTIFICADOR) {
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                if (i == null) {
                    aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.CONST, 0);
                    desplazamiento++;
                } else {
                    indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            if (aLex.getS() == Terminal.IGUAL) {
                aLex.escanear();

            } else {
                indicadorDeErrores.mostrar(5, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            if (aLex.getS() == Terminal.MENOS) {
                aLex.escanear();
                if (aLex.getS() == Terminal.NUMERO) {
                    try {
                        aSem.agregarValor(base + desplazamiento - 1, -1 * Integer.parseInt(aLex.getCad()));
                    } catch (ArithmeticException ex) {
                        indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
            } else if (aLex.getS() == Terminal.NUMERO) {
                try {
                    aSem.agregarValor(base + desplazamiento - 1, Integer.parseInt(aLex.getCad()));
                } catch (ArithmeticException ex) {
                    indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }

            while (aLex.getS() == Terminal.COMA) {
                aLex.escanear();
                if (aLex.getS() == Terminal.IDENTIFICADOR) {
                    IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                    if (i == null) {
                        aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.CONST, 0);
                        desplazamiento++;
                    } else {
                        indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                if (aLex.getS() == Terminal.IGUAL) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(5, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                if (aLex.getS() == Terminal.MENOS) {
                aLex.escanear();
                if (aLex.getS() == Terminal.NUMERO) {
                    try {
                        aSem.agregarValor(base + desplazamiento - 1, -1 * Integer.parseInt(aLex.getCad()));
                    } catch (ArithmeticException ex) {
                        indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
            } else if (aLex.getS() == Terminal.NUMERO) {
                    try {
                        aSem.agregarValor(base + desplazamiento - 1, Integer.parseInt(aLex.getCad()));
                    } catch (ArithmeticException ex) {
                        indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(6, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
            }
            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
        }

        if (aLex.getS() == Terminal.VAR) {
            aLex.escanear();
            if (aLex.getS() == Terminal.IDENTIFICADOR) {
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                if (i == null) {
                    aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.VAR, valorVar);
                    valorVar = valorVar + 4;
                    desplazamiento++;
                } else {
                    indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }

                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            while (aLex.getS() == Terminal.COMA) {
                aLex.escanear();
                if (aLex.getS() == Terminal.IDENTIFICADOR) {
                    IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                    if (i == null) {
                        aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.VAR, valorVar);
                        valorVar = valorVar + 4;
                        desplazamiento++;
                    } else {
                        indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }

                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
            }
            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
        }

        while (aLex.getS() == Terminal.PROCEDURE) {
            aLex.escanear();
            if (aLex.getS() == Terminal.IDENTIFICADOR) {
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, base);
                if (i == null) {
                    aSem.cargar(base + desplazamiento, aLex.getCad(), Terminal.PROCEDURE, generadorDeCodigo.getTopeMemoria());
                    desplazamiento++;
                } else {
                    indicadorDeErrores.mostrar(15, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }

                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
            bloque(base + desplazamiento);
            generadorDeCodigo.cargarByte(0xC3);//RET

            if (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                aLex.escanear();
            } else {
                indicadorDeErrores.mostrar(4, aLex.getCad(), aLex.getFila(), aLex.getColumna());
            }
        }
        int dis = generadorDeCodigo.getTopeMemoria() - iniSalto;
        generadorDeCodigo.cargarIntEn(dis, iniSalto - 4);
        proposicion(base, desplazamiento);
    }

    private void proposicion(int base, int desplazamiento) throws IOException {
        IdentificadorBean i;
        switch (aLex.getS()) {
            case IDENTIFICADOR:
                i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, 0);
                if (i == null) {
                    indicadorDeErrores.mostrar(16, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                } else {
                    if (i.getTipo() != Terminal.VAR) {
                        indicadorDeErrores.mostrar(17, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                }
                aLex.escanear();
                if (aLex.getS() == Terminal.ASIGNACION) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(7, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                expresion(base, desplazamiento);
                generadorDeCodigo.cargarPopEax();// POP EAX
                generadorDeCodigo.cargarByte(0x89);// MOV [EDI + ...], EAX
                generadorDeCodigo.cargarByte(0x87);
                generadorDeCodigo.cargarInt(i.getValor());

                break;
            case CALL:
                aLex.escanear();
                if (aLex.getS() == Terminal.IDENTIFICADOR) {
                    i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, 0);
                    if (i == null) {
                        indicadorDeErrores.mostrar(16, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    } else {
                        if (i.getTipo() != Terminal.PROCEDURE) {
                            indicadorDeErrores.mostrar(18, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                        } else {
                            int dis = i.getValor() - (generadorDeCodigo.getTopeMemoria() + 5); // 
                            generadorDeCodigo.cargarByte(0xE8); // CALL ...
                            generadorDeCodigo.cargarInt(dis); // 
                        }
                    }
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                break;
            case BEGIN:
                aLex.escanear();
                proposicion(base, desplazamiento);
                while (aLex.getS() == Terminal.PUNTO_Y_COMA) {
                    aLex.escanear();
                    proposicion(base, desplazamiento);
                }
                if (aLex.getS() == Terminal.END) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(8, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                break;
            case IF:
                aLex.escanear();
                condicion(base, desplazamiento);
                int finCondicion = generadorDeCodigo.getTopeMemoria();
                if (aLex.getS() == Terminal.THEN) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(9, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                proposicion(base, desplazamiento);
                int finProposicion = generadorDeCodigo.getTopeMemoria();
                int dis = finProposicion - finCondicion;
                generadorDeCodigo.cargarIntEn(dis, finCondicion - 4);
                break;
            case WHILE:
                aLex.escanear();
                int inicioCondicion = generadorDeCodigo.getTopeMemoria();
                condicion(base, desplazamiento);
                finCondicion = generadorDeCodigo.getTopeMemoria();
                if (aLex.getS() == Terminal.DO) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(10, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                proposicion(base, desplazamiento);
                finProposicion = generadorDeCodigo.getTopeMemoria();
                dis = inicioCondicion - (finProposicion + 5);
                generadorDeCodigo.cargarByte(0xE9);//JMP
                generadorDeCodigo.cargarInt(dis);
                int finWhile = generadorDeCodigo.getTopeMemoria();
                dis = finWhile - finCondicion;
                generadorDeCodigo.cargarIntEn(dis, finCondicion - 4);
                break;
            case READLN:
                aLex.escanear();
                if (aLex.getS() == Terminal.ABRE_PARENTESIS) {

                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(11, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                if (aLex.getS() == Terminal.IDENTIFICADOR) {

                    i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, 0);
                    if (i == null) {
                        indicadorDeErrores.mostrar(16, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    } else {
                        if (i.getTipo() != Terminal.VAR) {
                            indicadorDeErrores.mostrar(17, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                        } else {

                            dis = 0x310 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                            generadorDeCodigo.cargarByte(0xE8); // CALL 0x0590
                            generadorDeCodigo.cargarInt(dis); // 
                            generadorDeCodigo.cargarByte(0x89); // MOV [EDI + ...], EAX
                            generadorDeCodigo.cargarByte(0x87);
                            generadorDeCodigo.cargarInt(i.getValor());
                        }
                    }

                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                while (aLex.getS() == Terminal.COMA) {
                    aLex.escanear();
                    if (aLex.getS() == Terminal.IDENTIFICADOR) {

                        i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, 0);
                        if (i == null) {
                            indicadorDeErrores.mostrar(16, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                        } else {
                            if (i.getTipo() != Terminal.VAR) {
                                indicadorDeErrores.mostrar(17, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                            } else {

                                dis = 0x310 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                                generadorDeCodigo.cargarByte(0xE8); // CALL 0x0590
                                generadorDeCodigo.cargarInt(dis); // 
                                generadorDeCodigo.cargarByte(0x89); // MOV [EDI + ...], EAX
                                generadorDeCodigo.cargarByte(0x87);
                                generadorDeCodigo.cargarInt(i.getValor());
                            }
                        }
                        aLex.escanear();
                    } else {
                        indicadorDeErrores.mostrar(3, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                }
                if (aLex.getS() == Terminal.CIERRA_PARENTESIS) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(12, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }

                break;
            case WRITE:
                aLex.escanear();
                if (aLex.getS() == Terminal.ABRE_PARENTESIS) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(11, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                if (aLex.getS() == Terminal.CADENA_LITERAL) {

                    /*
                    ***WINDOWS***
                    int ubiCade = generadorDeCodigo.leerIntEn(212)
                            +//image base
                            generadorDeCodigo.leerIntEn(204)
                            +//base code
                            generadorDeCodigo.getTopeMemoria()
                            + 15 - Constantes.TAMANIOENCABEZADOWINDOWS;
                    
                    ***WINDOWS***        
                     */
                    //generadorDeCodigo.cargarByte(0xB8); // MOV EAX,... WINDOWS
                    //***Linux****
                    int ubiCade = generadorDeCodigo.leerIntEn(193) + generadorDeCodigo.getTopeMemoria()
                            + 20 - Constantes.TAMANIOENCABEZADOLINUX;

                    generadorDeCodigo.cargarByte(0xB9); // MOV ECX,... Linux
                    generadorDeCodigo.cargarInt(ubiCade);

                    String cadena = aLex.getCad();
                    dis = cadena.length() - 2; // LONGITUD DE LA CADENA

                    generadorDeCodigo.cargarByte(0xBA); // MOV EDX,... Linux
                    generadorDeCodigo.cargarInt(dis);

                    dis = 0x170 - (generadorDeCodigo.getTopeMemoria() + 5); // Direccion de E/S Linux
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x170
                    generadorDeCodigo.cargarInt(dis);

                    dis = cadena.length() - 2; // LONGITUD DE LA CADENA
                    generadorDeCodigo.cargarByte(0xE9);//JMP
                    generadorDeCodigo.cargarInt(dis);

                    for (int j = 1; j <= dis; j++) {
                        generadorDeCodigo.cargarByte(cadena.charAt(j));
                    }

                    // *** WINDOWS //
                    /*
                    dis = 0x3E0 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x3E0
                    generadorDeCodigo.cargarInt(dis); // 
                    String cadena = aLex.getCad();
                    dis = cadena.length() - 1;
                    generadorDeCodigo.cargarByte(0xE9);//JMP
                    generadorDeCodigo.cargarInt(dis);
                    
                    for (int j = 1; j < dis; j++) {
                        generadorDeCodigo.cargarByte(cadena.charAt(j));
                    }
                    
                     */
                    // *** WINDOWS ***
                    aLex.escanear();
                } else {
                    expresion(base, desplazamiento);
                    /* *** WINDOWS ***
                    generadorDeCodigo.cargarPopEax(); // POP EAX
                    dis = 0x420 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x420
                    generadorDeCodigo.cargarInt(dis); // 
                    *** WINDOWS ***
                     */
                    //**** LINUX
                    generadorDeCodigo.cargarPopEax(); // POP EAX
                    dis = 0x190 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x190
                    generadorDeCodigo.cargarInt(dis); // 
                    // *****
                }
                while (aLex.getS() == Terminal.COMA) {
                    aLex.escanear();
                    if (aLex.getS() == Terminal.CADENA_LITERAL) {

                        /*
                    ***WINDOWS***
                    int ubiCade = generadorDeCodigo.leerIntEn(212)
                            +//image base
                            generadorDeCodigo.leerIntEn(204)
                            +//base code
                            generadorDeCodigo.getTopeMemoria()
                            + 20 - Constantes.TAMANIOENCABEZADOWINDOWS;
                    
                    ***WINDOWS***        
                         */
                        //generadorDeCodigo.cargarByte(0xB8); // MOV EAX,... WINDOWS
                        //***Linux****
                        int ubiCade = generadorDeCodigo.leerIntEn(193) + generadorDeCodigo.getTopeMemoria()
                                + 20 - Constantes.TAMANIOENCABEZADOLINUX;

                        generadorDeCodigo.cargarByte(0xB9); // MOV ECX,... Linux
                        generadorDeCodigo.cargarInt(ubiCade);

                        String cadena = aLex.getCad();
                        dis = cadena.length() - 2; // LONGITUD DE LA CADENA

                        generadorDeCodigo.cargarByte(0xBA); // MOV EDX,... Linux
                        generadorDeCodigo.cargarInt(dis);

                        dis = 0x170 - (generadorDeCodigo.getTopeMemoria() + 5); // Direccion de E/S Linux
                        generadorDeCodigo.cargarByte(0xE8); // CALL 0x170
                        generadorDeCodigo.cargarInt(dis);

                        dis = cadena.length() - 2; // LONGITUD DE LA CADENA
                        generadorDeCodigo.cargarByte(0xE9);//JMP
                        generadorDeCodigo.cargarInt(dis);

                        for (int j = 1; j <= dis; j++) {
                            generadorDeCodigo.cargarByte(cadena.charAt(j));
                        }

                        // *** WINDOWS //
                        /*
                    dis = 0x3E0 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x3E0
                    generadorDeCodigo.cargarInt(dis); // 
                    String cadena = aLex.getCad();
                    dis = cadena.length() - 1;
                    generadorDeCodigo.cargarByte(0xE9);//JMP
                    generadorDeCodigo.cargarInt(dis);
                    
                    for (int j = 1; j < dis; j++) {
                        generadorDeCodigo.cargarByte(cadena.charAt(j));
                    }
                    
                         */
                        // *** WINDOWS ***
                        aLex.escanear();
                    } else {
                        expresion(base, desplazamiento);
                        /* *** WINDOWS ***
                    generadorDeCodigo.cargarPopEax(); // POP EAX
                    dis = 0x420 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x420
                    generadorDeCodigo.cargarInt(dis); // 
                    *** WINDOWS ***
                         */
                        //**** LINUX
                        generadorDeCodigo.cargarPopEax(); // POP EAX
                        dis = 0x190 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                        generadorDeCodigo.cargarByte(0xE8); // CALL 0x190
                        generadorDeCodigo.cargarInt(dis); // 
                        // *****

                    }
                }
                if (aLex.getS() == Terminal.CIERRA_PARENTESIS) {
                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(12, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                break;
            case WRITELN:
                aLex.escanear();
                if (aLex.getS() == Terminal.ABRE_PARENTESIS) {
                    aLex.escanear();
                    if (aLex.getS() == Terminal.CADENA_LITERAL) {
                        /*
                    ***WINDOWS***
                    int ubiCade = generadorDeCodigo.leerIntEn(212)
                            +//image base
                            generadorDeCodigo.leerIntEn(204)
                            +//base code
                            generadorDeCodigo.getTopeMemoria()
                            + 20 - Constantes.TAMANIOENCABEZADOWINDOWS;
                    
                    ***WINDOWS***        
                         */

                        //generadorDeCodigo.cargarByte(0xB8); // MOV EAX,... WINDOWS
                        //***Linux****
                        int ubiCade = generadorDeCodigo.leerIntEn(193) + generadorDeCodigo.getTopeMemoria()
                                + 20 - Constantes.TAMANIOENCABEZADOLINUX;

                        generadorDeCodigo.cargarByte(0xB9); // MOV ECX,... Linux
                        generadorDeCodigo.cargarInt(ubiCade);

                        String cadena = aLex.getCad();
                        dis = cadena.length() - 2; // LONGITUD DE LA CADENA

                        generadorDeCodigo.cargarByte(0xBA); // MOV EDX,... Linux
                        generadorDeCodigo.cargarInt(dis);

                        dis = 0x170 - (generadorDeCodigo.getTopeMemoria() + 5); // Direccion de E/S Linux
                        generadorDeCodigo.cargarByte(0xE8); // CALL 0x170
                        generadorDeCodigo.cargarInt(dis);

                        dis = cadena.length() - 2; // LONGITUD DE LA CADENA
                        generadorDeCodigo.cargarByte(0xE9);//JMP
                        generadorDeCodigo.cargarInt(dis);

                        for (int j = 1; j <= dis; j++) {
                            generadorDeCodigo.cargarByte(cadena.charAt(j));
                        }

                        // *** WINDOWS //
                        /*
                    dis = 0x3E0 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x3E0
                    generadorDeCodigo.cargarInt(dis); // 
                    String cadena = aLex.getCad();
                    dis = cadena.length() - 1;
                    generadorDeCodigo.cargarByte(0xE9);//JMP
                    generadorDeCodigo.cargarInt(dis);
                    
                    for (int j = 1; j < dis; j++) {
                        generadorDeCodigo.cargarByte(cadena.charAt(j));
                    }
                    
                         */
                        // *** WINDOWS ***
                        aLex.escanear();
                    } else {
                        expresion(base, desplazamiento);
                        /* *** WINDOWS ***
                    generadorDeCodigo.cargarPopEax(); // POP EAX
                    dis = 0x420 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x420
                    generadorDeCodigo.cargarInt(dis); // 
                    *** WINDOWS ***
                         */
                        //**** LINUX
                        generadorDeCodigo.cargarPopEax(); // POP EAX
                        dis = 0x190 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                        generadorDeCodigo.cargarByte(0xE8); // CALL 0x190
                        generadorDeCodigo.cargarInt(dis); // 
                        // *****
                    }
                    while (aLex.getS() == Terminal.COMA) {
                        aLex.escanear();
                        if (aLex.getS() == Terminal.CADENA_LITERAL) {
                            /*
                    ***WINDOWS***
                    int ubiCade = generadorDeCodigo.leerIntEn(212)
                            +//image base
                            generadorDeCodigo.leerIntEn(204)
                            +//base code
                            generadorDeCodigo.getTopeMemoria()
                            + 20 - Constantes.TAMANIOENCABEZADOWINDOWS;
                    
                    ***WINDOWS***        
                             */

                            //generadorDeCodigo.cargarByte(0xB8); // MOV EAX,... WINDOWS
                            //***Linux****
                            int ubiCade = generadorDeCodigo.leerIntEn(193) + generadorDeCodigo.getTopeMemoria()
                                    + 20 - Constantes.TAMANIOENCABEZADOLINUX;

                            generadorDeCodigo.cargarByte(0xB9); // MOV ECX,... Linux
                            generadorDeCodigo.cargarInt(ubiCade);

                            String cadena = aLex.getCad();
                            dis = cadena.length() - 2; // LONGITUD DE LA CADENA

                            generadorDeCodigo.cargarByte(0xBA); // MOV EDX,... Linux
                            generadorDeCodigo.cargarInt(dis);

                            dis = 0x170 - (generadorDeCodigo.getTopeMemoria() + 5); // Direccion de E/S Linux
                            generadorDeCodigo.cargarByte(0xE8); // CALL 0x170
                            generadorDeCodigo.cargarInt(dis);

                            dis = cadena.length() - 2; // LONGITUD DE LA CADENA
                            generadorDeCodigo.cargarByte(0xE9);//JMP
                            generadorDeCodigo.cargarInt(dis);

                            for (int j = 1; j <= dis; j++) {
                                generadorDeCodigo.cargarByte(cadena.charAt(j));
                            }

                            // *** WINDOWS //
                            /*
                    dis = 0x3E0 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x3E0
                    generadorDeCodigo.cargarInt(dis); // 
                    String cadena = aLex.getCad();
                    dis = cadena.length() - 1;
                    generadorDeCodigo.cargarByte(0xE9);//JMP
                    generadorDeCodigo.cargarInt(dis);
                    
                    for (int j = 1; j < dis; j++) {
                        generadorDeCodigo.cargarByte(cadena.charAt(j));
                    }
                    
                             */
                            // *** WINDOWS ***
                            aLex.escanear();
                        } else {
                            expresion(base, desplazamiento);
                            /* *** WINDOWS ***
                    generadorDeCodigo.cargarPopEax(); // POP EAX
                    dis = 0x420 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                    generadorDeCodigo.cargarByte(0xE8); // CALL 0x420
                    generadorDeCodigo.cargarInt(dis); // 
                    *** WINDOWS ***
                             */
                            //**** LINUX
                            generadorDeCodigo.cargarPopEax(); // POP EAX
                            dis = 0x190 - (generadorDeCodigo.getTopeMemoria() + 5); // 
                            generadorDeCodigo.cargarByte(0xE8); // CALL 0x190
                            generadorDeCodigo.cargarInt(dis); // 
                            // *****
                        }
                    }
                    if (aLex.getS() == Terminal.CIERRA_PARENTESIS) {
                        aLex.escanear();
                    } else {
                        indicadorDeErrores.mostrar(12, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }
                }
                // dis = 0x410 - (generadorDeCodigo.getTopeMemoria() + 5); // SALTO DE LINEA EN WINDOWS
                dis = 0x180 - (generadorDeCodigo.getTopeMemoria() + 5); // SALTO DE LINEA EN LINUX
                generadorDeCodigo.cargarByte(0xE8); // CALL 0x180
                generadorDeCodigo.cargarInt(dis); // 
        }
    }

    private void condicion(int base, int desplazamiento) throws IOException {
        if (aLex.getS() == Terminal.ODD) {

            aLex.escanear();
            expresion(base, desplazamiento);
            generadorDeCodigo.cargarPopEax(); // POP EAX
            generadorDeCodigo.cargarByte(0xA8); // TEST AL, ...
            generadorDeCodigo.cargarByte(0x01);
            generadorDeCodigo.cargarByte(0x7B); // JPO ...
            generadorDeCodigo.cargarByte(0x05);
            generadorDeCodigo.cargarByte(0xE9); // JMP ...
            generadorDeCodigo.cargarInt(0);
            // 58 A8 01 7B 05 E9 00 00 00 00
        } else {
            expresion(base, desplazamiento);
            Terminal t = aLex.getS();
            switch (aLex.getS()) {
                case IGUAL:
                    aLex.escanear();
                    break;
                case MAYOR:
                    aLex.escanear();
                    break;
                case MENOR:
                    aLex.escanear();
                    break;
                case MAYOR_IGUAL:
                    aLex.escanear();
                    break;
                case MENOR_IGUAL:
                    aLex.escanear();
                    break;
                case DISTINTO:
                    aLex.escanear();
                    break;
                default:
                    indicadorDeErrores.mostrar(13, aLex.getCad(), aLex.getFila(), aLex.getColumna());

            }
            expresion(base, desplazamiento);
            generadorDeCodigo.cargarPopEax(); // POP EAX
            generadorDeCodigo.cargarByte(0x5B); // POP EBX
            generadorDeCodigo.cargarByte(0x39); // CMP EBX, EAX
            generadorDeCodigo.cargarByte(0xC3);
            switch (t) {
                case IGUAL:
                    generadorDeCodigo.cargarByte(0x74); // JE ...
                    break;
                case DISTINTO:
                    generadorDeCodigo.cargarByte(0x75); // JNE ...

                    break;
                case MENOR:
                    generadorDeCodigo.cargarByte(0x7C); // JL ...

                    break;
                case MENOR_IGUAL:
                    generadorDeCodigo.cargarByte(0x7E); // JLE ...

                    break;
                case MAYOR:
                    generadorDeCodigo.cargarByte(0x7F); // JG ...

                    break;
                case MAYOR_IGUAL:
                    generadorDeCodigo.cargarByte(0x7D); // JGE ...

                    break;
            }
            generadorDeCodigo.cargarByte(0x05);
            generadorDeCodigo.cargarByte(0xE9); // JMP ...
            generadorDeCodigo.cargarInt(0);
        }

    }

    private void expresion(int base, int desplazamiento) throws IOException {
        Terminal t = null;
        if (aLex.getS() == Terminal.MAS) {
            aLex.escanear();
        } else if (aLex.getS() == Terminal.MENOS) {
            t = aLex.getS();
            aLex.escanear();
        }
        termino(base, desplazamiento);

        if (t == Terminal.MENOS) {
            generadorDeCodigo.cargarPopEax(); // POP EAX
            generadorDeCodigo.cargarByte(0xF7); // NEG EAX
            generadorDeCodigo.cargarByte(0xD8);
            generadorDeCodigo.cargarByte(0x50); // PUSH EAX
        }
        while (aLex.getS() == Terminal.MAS || aLex.getS() == Terminal.MENOS) {
            t = aLex.getS();
            aLex.escanear();
            termino(base, desplazamiento);
            if (t == Terminal.MENOS) {
                generadorDeCodigo.cargarPopEax(); // POP EAX
                generadorDeCodigo.cargarByte(0x5B); // POP EBX
                generadorDeCodigo.cargarByte(0x93); // XCHG EAX, EBX
                generadorDeCodigo.cargarByte(0x29); // SUB EAX, EBX
                generadorDeCodigo.cargarByte(0xD8);
                generadorDeCodigo.cargarByte(0x50); // PUSH EAX
            } else {
                generadorDeCodigo.cargarPopEax(); // POP EAX
                generadorDeCodigo.cargarByte(0x5B); // POP EBX
                generadorDeCodigo.cargarByte(0x01); // ADD EAX, EBX
                generadorDeCodigo.cargarByte(0xD8);
                generadorDeCodigo.cargarByte(0x50); // PUSH EAX

            }
        }
    }

    private void termino(int base, int desplazamiento) throws IOException {
        factor(base, desplazamiento);
        while (aLex.getS() == Terminal.DIVIDIDO || aLex.getS() == Terminal.POR) {
            Terminal t = aLex.getS();
            aLex.escanear();
            factor(base, desplazamiento);
            if (t == Terminal.DIVIDIDO) {
                generadorDeCodigo.cargarPopEax(); // POP EAX
                generadorDeCodigo.cargarByte(0x5B); // POP EBX
                generadorDeCodigo.cargarByte(0x93); // XCHG EAX, EBX
                generadorDeCodigo.cargarByte(0x99); // CDQ
                generadorDeCodigo.cargarByte(0xF7); // IDIV EBX
                generadorDeCodigo.cargarByte(0xFB);
                generadorDeCodigo.cargarByte(0x50); // PUSH EAX
            } else {
                generadorDeCodigo.cargarPopEax(); // POP EAX
                generadorDeCodigo.cargarByte(0x5B); // POP EBX
                generadorDeCodigo.cargarByte(0xF7); // IMUL EBX
                generadorDeCodigo.cargarByte(0xEB);
                generadorDeCodigo.cargarByte(0x50); // PUSH EAX                
            }
        }

    }

    private void factor(int base, int desplazamiento) throws IOException {
        switch (aLex.getS()) {
            case IDENTIFICADOR:
                IdentificadorBean i = aSem.buscar(aLex.getCad(), base + desplazamiento - 1, 0);
                if (i == null) {
                    indicadorDeErrores.mostrar(16, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                } else {
                    if (i.getTipo() == Terminal.PROCEDURE) {
                        indicadorDeErrores.mostrar(19, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    } else {
                        if (i.getTipo() == Terminal.CONST) {
                            generadorDeCodigo.cargarByte(0xB8);// MOV EAX,...
                            generadorDeCodigo.cargarInt(i.getValor());
                            generadorDeCodigo.cargarByte(0x50); // PUSH EAX
                        } else {
                            generadorDeCodigo.cargarByte(0x8B);// MOV EAX, [EDI + ...]
                            generadorDeCodigo.cargarByte(0x87);
                            generadorDeCodigo.cargarInt(i.getValor());
                            generadorDeCodigo.cargarByte(0x50); // PUSH EAX
                        }
                    }
                }
                aLex.escanear();
                break;

            case NUMERO:
                generadorDeCodigo.cargarByte(0xB8);// MOV EAX,...
                try {
                    generadorDeCodigo.cargarInt(Integer.parseInt(aLex.getCad()));
                } catch (ArithmeticException ex) {
                    indicadorDeErrores.mostrar(20, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                generadorDeCodigo.cargarByte(0x50); // PUSH EAX
                aLex.escanear();
                break;

            case ABRE_PARENTESIS:
                aLex.escanear();
                expresion(base, desplazamiento);
                if (aLex.getS() == Terminal.CIERRA_PARENTESIS) {

                    aLex.escanear();
                } else {
                    indicadorDeErrores.mostrar(12, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                }
                break;
            case SQR:

                aLex.escanear();
                if (aLex.getS() == Terminal.ABRE_PARENTESIS) {
                    aLex.escanear();
                    expresion(base, desplazamiento);

                    generadorDeCodigo.cargarByte(0x58); // POP EAX
                    generadorDeCodigo.cargarByte(0x93); // XCHG EAX EBX
                    generadorDeCodigo.cargarByte(0xB8); // MOV EAX ...
                    generadorDeCodigo.cargarInt(0); // Cargamos 0 en EAX
                    generadorDeCodigo.cargarByte(0x01); // Sumamos EAX y EBX 
                    generadorDeCodigo.cargarByte(0xD8); 
                    generadorDeCodigo.cargarByte(0xF7); // IMUL EBX = EAX * EBX
                    generadorDeCodigo.cargarByte(0xEB);
                    generadorDeCodigo.cargarByte(0x50); // PUSH EAX

                    if (aLex.getS() == Terminal.CIERRA_PARENTESIS) {
                        aLex.escanear();
                    } else {
                        indicadorDeErrores.mostrar(12, aLex.getCad(), aLex.getFila(), aLex.getColumna());
                    }

                }
                break;
            default:
                indicadorDeErrores.mostrar(14, aLex.getCad(), aLex.getFila(), aLex.getColumna());
        }
    }

}
