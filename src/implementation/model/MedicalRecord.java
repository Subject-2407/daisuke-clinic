package implementation.model;

import implementation.model.interfaces.Identifiable;

// wip

public class MedicalRecord implements Identifiable {
    private int id;
    
    @Override
    public int getId() { return id; }
}