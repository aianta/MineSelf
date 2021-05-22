package ca.mineself;


import android.util.Log;

import com.influxdb.client.BucketsApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.OrganizationsApi;
import com.influxdb.client.WriteApi;
import com.influxdb.client.WriteOptions;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;
import com.influxdb.client.domain.Query;
import com.influxdb.client.domain.User;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import ca.mineself.model.Aspect;
import ca.mineself.model.Profile;

public class Influx {

    public static class InfluxRequest<T> implements Callable<T> {
        private Supplier<T> request;

        public InfluxRequest(Supplier<T> request){
            this.request = request;
        }

        @Override
        public T call() throws Exception {
            return request.get();
        }
    }

    public static Organization findOrCreateOrg(InfluxDBClient client, String orgName){
        OrganizationsApi orgApi = client.getOrganizationsApi();
        return orgApi.findOrganizations()
                .stream()
                .peek(organization -> Log.d("Influx", "Org: " + organization.getName() + " Id:" + organization.getId()))
                .filter(organization -> organization.getName().equals(orgName))
                .findAny()
                .orElseGet(()->orgApi.createOrganization(orgName));
    }

    public static Bucket findOrCreateBucket(InfluxDBClient client, String bucketName, String orgId){
        BucketsApi bucketsApi = client.getBucketsApi();
        Log.d("Influx", "Buckets:");
        bucketsApi.findBuckets().forEach(bucket->Log.d("Influx", bucket.getName()));
        return bucketsApi.findBuckets()
                .stream()
                .filter(bucket -> bucket.getName().equals(bucketName))
                .findAny()
                .orElseGet(()->bucketsApi.createBucket(bucketName, orgId));
    }

    public static List<Aspect> getAspects(InfluxDBClient client, String bucketName, String orgName){

        String query = "import \"influxdata/influxdb/schema\" "+
                "schema.measurements(bucket:\""+ bucketName +"\")";

        List<FluxTable> tables = client.getQueryApi().query(query, orgName);
        FluxTable fluxTable = tables.get(0);
        return fluxTable.getRecords().stream()
                .map(record->record.getValueByKey("_value").toString())
                .peek(aspectName->Log.d("Influx", "aspect: " + aspectName))
                .map(aspectName->Influx.getAspectWithName(client,aspectName,bucketName,orgName))
                .collect(Collectors.toList());

    }

    private static Aspect getAspectWithName(InfluxDBClient client, String aspectName, String bucketName, String orgName){
        Aspect result = new Aspect();
        result.name = aspectName;

        String query = "from(bucket:\""+bucketName + "\")" +
                "|> range(start:1609518184, stop:now())" + //Start Fri Jan 01 2021 16:23:04 GMT+0000
                "|> filter(fn: (r)=> r._measurement == \""+aspectName+"\" and (r._field ==\"value\" or r._field == \"delta\")) " +
                "|> last()";

        try{
            List<FluxTable> tables = client.getQueryApi().query(query, orgName);
            for(FluxTable fluxTable: tables){
                switch (fluxTable.getRecords().get(0).getField()){
                    case "value":
                        result.value = (long)fluxTable.getRecords().get(0).getValue();
                        break;
                    case "delta":
                        result.delta = (long)fluxTable.getRecords().get(0).getValue();
                        break;
                    default:
                        Log.d("Influx", "Unrecognized aspect field "+fluxTable.getRecords().get(0).getField()+" while attempting to load "+ aspectName + " aspect");
                }
            }
        }catch (Exception e){
            Log.e("Influx", "Error getting aspect with name!");
            Log.e("Influx", e.getMessage(), e);
        }


        return result;
    }

    public static void insertPoint(InfluxDBClient client, String bucket, String org, Point point){

        try(WriteApi writeApi = client.getWriteApi()){
            writeApi.writePoint(bucket,org,point);
            writeApi.flush();

        }catch (Exception e){
            Log.d("Influx", e.getMessage(), e);
        }

    }


}
