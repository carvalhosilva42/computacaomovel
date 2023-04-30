package Model;
//import java.io.Serializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
//import java.util.UUID;

public class SensoresModel implements Serializable {
    private String ID;
    private static final long serialVersionUID = 1L;
    private Integer TipoSensor; //0/1 - pluviometro/estação solo
    private ArrayList<Float> Dados = new ArrayList<Float>();
    private ArrayList<Long> datas = new ArrayList<Long>();
    private Float latitude;
    private Float longitude;
    private UUID myUUID;
    public void setID(String id){
        this.ID=id;
    }
    public String getID() {
        return this.ID;
    }
    public void setTipoSensor(Integer tipo){
        this.TipoSensor=tipo;
    }
    public Integer getTipoSensor() {
        return this.TipoSensor;
    }
    public void setLatitude(Float latitude){
        this.latitude=latitude;
    }
    public void setLongitude(Float longitude){
        this.longitude=longitude;
    }
    public Float getLatitude(){
        return this.latitude;
    }
    public Float getLongitude(){
        return this.longitude;
    }

    public void setMyUUID(UUID myUUID) {
        this.myUUID = myUUID;
    }

    public UUID getMyUUID() {
        return myUUID;
    }

    public void setDados(Map<Long,Float> entrada) {
        Long hora_atual=0l;
        for (Long key : entrada.keySet()){
            hora_atual=key;
        }
        float valor=0f;
        for(float value:entrada.values()){
            valor=value;
        }
        if(this.datas.size()!=0){
            if((hora_atual-this.datas.get(this.datas.size()-1))>172800000) {
                this.Dados.remove(this.Dados.size() - 1);
                this.datas.remove(this.datas.size() - 1);
                this.Dados.add(0, valor);
                this.datas.add(0, hora_atual);
            } else{
                this.datas.add(0,hora_atual);
                this.Dados.add(0, valor);
            }
        }else{
            this.datas.add(0,hora_atual);
            this.Dados.add(0, valor);
        }
    }
    public Float get24h(){
        if(getTipoSensor()==1){
            return this.Dados.get(0);
        }
        float sum=0;
        Long hora_recente = this.datas.get(0);
        int indice=0;
        for(int i=1;i<this.datas.size();i++) {
            if ((hora_recente - this.datas.get(i)) > 86400000) {
                indice = i - 1;
                break;
            }
            indice=i;
        }
        for(int i=0;i<indice;i++){
            sum+=this.Dados.get(i);
        }
        return sum;
    }
    public Float get48h(){
        if(getTipoSensor()==1){
            return this.Dados.get(0);
        }
        float sum=0;
        for(int i=0;i<this.Dados.size();i++){
            sum+=this.Dados.get(i);
        }
        return sum;
    }
}
