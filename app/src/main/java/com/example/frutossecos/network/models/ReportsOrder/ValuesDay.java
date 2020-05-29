package com.example.frutossecos.network.models.ReportsOrder;

public class ValuesDay {
    public Double sumTot;
    public Double sumPendient;
    public Double sumDone;

    public ValuesDay(Double sumDone, Double sumPendient, Double sumTot ){
        this.sumDone=sumDone;
        this.sumPendient=sumPendient;
        this.sumTot=sumTot;
    }
}
