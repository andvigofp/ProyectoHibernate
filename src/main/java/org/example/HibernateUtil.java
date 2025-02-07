package org.example;

import java.io.File;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    private static final SessionFactory SESSION_FACTORY;

    static {
        try {
            String hibernatePropsFilePath = "./src/hibernate.cfg.xml"; // Ruta al fichero

            File hibernatePropsFile = new File(hibernatePropsFilePath);

            SESSION_FACTORY = new Configuration().configure(hibernatePropsFile).buildSessionFactory();

        }catch(Throwable ex) {
            System.err.println("Error al crear la configuración de hibernate" + ex.getMessage());
            throw new ExceptionInInitializerError();
        }
    }

    public static SessionFactory get() {
        return SESSION_FACTORY;
    }
}
