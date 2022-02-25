package com.juan;


import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Scanner;

public class Main {


    public static void main(String[] args) {
        Session session = null;
        ChannelExec channel = null;

        Scanner sc = new Scanner(System.in);

        System.out.println("ingrese una IP o un host");
        String ip = sc.next();
        System.out.println("ingrese un puerto a donde se quiere conectar ");
        int por = sc.nextInt();
        System.out.println("Ingrese un username ");
        String userName = sc.next();
        System.out.println("Ingrese la contraseña ");
        String password = sc.next();
        String fichero = "";
        boolean SALIR = false;
        String datoContenido = "";
        ByteArrayOutputStream datoStream = null;

        try {
            session = new JSch().getSession(userName,ip,por);
            session.setPassword(password);

            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channel = (ChannelExec) session.openChannel("exec");
            System.out.println("ingrese nombre de fichero de registro");
            fichero = sc.next();
            channel.setCommand("cd /var/log; cat " + fichero + ".log");

            datoStream = new ByteArrayOutputStream();
            channel.setOutputStream(datoStream);
            channel.connect();

            while (channel.isConnected()) {
                Thread.sleep(100);
            }

            datoContenido = new String(datoStream.toByteArray());

            if (datoContenido.equals("")) {
                System.out.println("error ese fichero no se encuentra en el log");
            } else {
                System.out.println(datoContenido);
            }

                while(!SALIR) {
                    System.out.println("opciones \n" +
                                   "1.-Añadir otro archivo \n"+
                                   "0.-Salir del programa");

                   int opcion = sc.nextInt();

                    switch (opcion) {
                        case 1:
                            channel = (ChannelExec) session.openChannel("exec");
                            System.out.println("ingrese nombre de fichero de registro");
                            fichero = sc.next();
                            channel.setCommand("cd /var/log; cat " + fichero + ".log");

                            datoStream = new ByteArrayOutputStream();
                            channel.setOutputStream(datoStream);
                            channel.connect();

                            while (channel.isConnected()) {
                               Thread.sleep(100);
                            }
                            datoContenido = new String(datoStream.toByteArray());
                            if (datoContenido.equals("")) {
                                System.out.println("error ese fichero no se encuentra en el log");
                            } else {
                                System.out.println(datoContenido);
                            }
                            break;
                        case 0:
                            SALIR = true;
                            break;
                        default:
                            System.out.println("el numero no esta dentro del rango");
                            break;
                    }

                }


        }catch (JSchException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (session != null) {
                session.disconnect();
            }
            if (channel != null) {
                channel.disconnect();
            }
        }

        sc.close();


    }
}
