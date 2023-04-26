import Model.SensoresModel;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class SensoresClient {
    private SensoresModel informacao;

    public static void main(String[] args) throws InterruptedException {

        ArrayList<SensoresClient> Sensores = new ArrayList<SensoresClient>();
        SensoresClient sensor1 =new SensoresClient("350970001A",0);
        SensoresClient sensor2 =new SensoresClient("350970001GP",0);
        SensoresClient sensor3 =new SensoresClient("350970003A",0);
        SensoresClient sensor4 =new SensoresClient("350970003GP",0);
        SensoresClient sensor5 =new SensoresClient("350970003GS",1);
        SensoresClient sensor6 =new SensoresClient("350970001A",0);

        Sensores.add(sensor1);
        Sensores.add(sensor2);
        Sensores.add(sensor3);
        Sensores.add(sensor4);
        Sensores.add(sensor5);
        Sensores.add(sensor6);
        ArrayList<ArrayList<Map<Long,Float>>> dados = new ArrayList<ArrayList<Map<Long,Float>>>();
        for(int i=0;i<Sensores.size();i++){
            dados.add(Sensores.get(i).obter_dados(Sensores.get(i).informacao.getID(),Sensores.get(i).informacao.getTipoSensor()));
        }
        int contador=0;
        while (true){
            System.out.println("-------------");
            for(int i=0;i<Sensores.size();i++){
                Map<Long,Float> dado = new HashMap<Long, Float>();
                long chave=0l;
                for (Long key : dados.get(i).get(contador).keySet()){
                    chave=key;
                }
                float valor=0f;
                for(float value:dados.get(i).get(contador).values()){
                    valor=value;
                }
                dado.put(chave,valor);
                Sensores.get(i).informacao.setDados(dado);
            }
            System.out.println(Sensores.get(4).informacao.get24h());
            contador++;
            TimeUnit.SECONDS.sleep(1);
        }
    }
    public SensoresClient(String id, Integer tipo){
        informacao =new SensoresModel();
        informacao.setTipoSensor(tipo);
        informacao.setID(id);

    }
    //public ArrayList<Float> obter_dados (String id){
    public ArrayList<Map<Long,Float>> obter_dados (String id, Integer tipo){
        String path = "C:\\Users\\carva\\IdeaProjects\\untitled\\src\\Dados\\";
        String arquivo = id;
        String line = "";
        ArrayList<Map<Long,Float>> retorno = new ArrayList<Map<Long,Float>>();
        try {
            if(tipo==0) {
                BufferedReader br = new BufferedReader(new FileReader(path + arquivo + ".csv"));

                while ((line = br.readLine()) != null) {

                    Map<Long, Float> dados = new HashMap<Long, Float>();
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date data = sf.parse(Arrays.stream(line.split(";")).toList().get(6));

                        float dado = Float.parseFloat(Arrays.stream(line.split(";")).toList().get(7).replace(",", "."));

                        dados.put(data.getTime(), dado);
                        retorno.add(dados);
                    } catch (Exception e) {
                        System.out.println(e);
                    } finally {
                        continue;
                    }
                }
            }else{
                BufferedReader br = new BufferedReader(new FileReader(path + arquivo + ".csv"));
                long aux= 0l;
                float sum=0f;
                int contador=0;
                while ((line = br.readLine()) != null) {
                    Map<Long, Float> dados = new HashMap<Long, Float>();
                    try {
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date data = sf.parse(Arrays.stream(line.split(";")).toList().get(6));
                        float dado = Float.parseFloat(Arrays.stream(line.split(";")).toList().get(8).replace(",", "."));
                        if(retorno.size()==0){
                            aux=data.getTime();
                        }
                        if((aux-data.getTime())==0){
                            if(Arrays.stream(line.split(";")).toList().get(7)!="chuva") {
                                sum+=(6-contador)*dado;
                                contador++;
                            }
                            if(contador==7){
                                dados.put(data.getTime(), sum/21);
                                retorno.add(dados);
                                contador=0;
                                sum=0;
                            }
                        }else{
                            aux=data.getTime();
                        }



                    } catch (Exception e) {

                        System.out.println(e);
                    } finally {
                        continue;
                    }
                }
            }
            }catch(FileNotFoundException e){
                e.printStackTrace();
            }catch (IOException e){
                e.printStackTrace();
            }
        return retorno;
    }
}
