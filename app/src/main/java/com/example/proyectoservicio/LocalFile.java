package com.example.proyectoservicio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

public class LocalFile {
    private final String path;
    private final File parent;
    public LocalFile(File parent, String path){
        this.path = path;
        this.parent = parent;

    }
    public void saveMedidor(Medidor medidor) {
        FileOutputStream outStream;
        try {
            File f = new File(parent,path+"medidor.dat");
            outStream = new FileOutputStream(f);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(medidor);
            objectOutStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public Medidor loadMedidor()
    {
        FileInputStream inStream;
        try {
            File f = new File(parent, path+"medidor.dat");
            inStream = new FileInputStream(f);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            Medidor medidor = (Medidor) objectInStream.readObject();
            objectInStream.close();
            return medidor;
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
    public void saveMedidores(List<Medidor>medidores) {
        FileOutputStream outStream;
        try {
            File f = new File(parent,path+"medidores.dat");
            outStream = new FileOutputStream(f);
            ObjectOutputStream objectOutStream = new ObjectOutputStream(outStream);
            objectOutStream.writeObject(medidores);
            objectOutStream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
    public List<Medidor> loadMedidores()
    {
        FileInputStream inStream;
        try {
            File f = new File(parent, path+"medidores.dat");
            inStream = new FileInputStream(f);
            ObjectInputStream objectInStream = new ObjectInputStream(inStream);
            List<Medidor> medidores= (List<Medidor>) objectInStream.readObject();
            objectInStream.close();
            return medidores;
        } catch (ClassNotFoundException | IOException e1) {
            e1.printStackTrace();
        }
        return null;
    }
}
