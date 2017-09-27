/*
 * TrainsDaoImplements
 * Author : Mikey-2017/09/08
 * 
 */
package com.lavidatec.template.dao;

import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import com.lavidatec.template.comment.JPAUtil;
import com.lavidatec.template.entity.TrainsModel;
import com.lavidatec.template.vo.TrainsVo;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository("ITrainsDao")
public class TrainsDaoImpl implements ITrainsDao{
    /**
     * log init.
     */
    static final Logger LOGGER
            = LoggerFactory.getLogger(TrainsDaoImpl.class);
    
    // Refer to table metadata names by byte array in the HBase API
    private static final byte[] TABLE_NAME = Bytes.toBytes("unit_trains");
    private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("trainInformation");
    private static final String[] COLUMN_NAME = {"type",
                                                    "date",
                                                    "price",
                                                    "ticketsLimit"};
    
    private static final String projectId = "plucky-lane-147516";
    private static final String instanceId = "quickstart-instance";
    
    @Override
    public final int trainsInsert(
            final Optional<TrainsModel> trainsOptional)
            throws Exception {
        LOGGER.info("Start trainsInsert");
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            
            ObjectMapper m = new ObjectMapper();
            Map<String,Object> props = m.convertValue(trainsOptional.get(), Map.class);
            // The admin API lets us create, manage and delete tables
            Admin admin = connection.getAdmin();
            // [END connecting_to_bigtable]

            // [START writing_rows]
            // Retrieve the table we just created so we can do some reads and writes
            Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
            System.out.println("Table exist: " + admin.tableExists(TableName.valueOf(TABLE_NAME)));
            if(!admin.tableExists(TableName.valueOf(TABLE_NAME))){
                // [START creating_a_table]
                // Create a table with a single column family
                HTableDescriptor descriptor = new HTableDescriptor(TableName.valueOf(TABLE_NAME));
                descriptor.addFamily(new HColumnDescriptor(COLUMN_FAMILY_NAME));

                System.out.println("Create table " + descriptor.getNameAsString());
                admin.createTable(descriptor);
                // [END creating_a_table]
                
                table = connection.getTable(TableName.valueOf(TABLE_NAME));
            }
            
            // Write some rows to the table
            System.out.println("Write some greetings to the table");
            for (int i = 0; i < COLUMN_NAME.length; i++) {
                // Each row has a unique row key.
                //
                // Note: This example uses sequential numeric IDs for simplicity, but
                // this can result in poor performance in a production application.
                // Since rows are stored in sorted order by key, sequential keys can
                // result in poor distribution of operations across nodes.
                //
                // For more information about how to design a Bigtable schema for the
                // best performance, see the documentation:
                //
                //     https://cloud.google.com/bigtable/docs/schema-design
                if(props.get(COLUMN_NAME[i]) != null){
                    String rowKey = trainsOptional.get().getNo().toString();
                    // Put a single row into the table. We could also pass a list of Puts to write a batch.
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes(COLUMN_NAME[i]), Bytes.toBytes(props.get(COLUMN_NAME[i]).toString()));
                    table.put(put);
                }
            }
        } catch (IOException e) {
            System.err.println("Exception while running trainsInsert: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("End trainsInsert");
        return 1;
    }

    @Override
    public final int trainsUpdate(
            final Optional<TrainsModel> trainsOptional)
            throws Exception {
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            
            ObjectMapper m = new ObjectMapper();
            Map<String,String> props = m.convertValue(trainsOptional.get(), Map.class);
//            MyBean anotherBean = m.convertValue(props, MyBean.class);
            System.out.println(props.toString());
            // The admin API lets us create, manage and delete tables
            Admin admin = connection.getAdmin();
            // [END connecting_to_bigtable]

            // Retrieve the table we just created so we can do some reads and writes
            Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
            System.out.println("Table exist: " + admin.tableExists(TableName.valueOf(TABLE_NAME)));
            if(!admin.tableExists(TableName.valueOf(TABLE_NAME))){
                return 0;
            }
            
            // [START getting_a_row]
            // Get the first greeting by row key
            String rowKey = trainsOptional.get().getNo();
            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
            if(getResult == null)
                return 0;
            // [END getting_a_row]
            
            // Write some rows to the table
            System.out.println("Write some greetings to the table");
            for (int i = 0; i < COLUMN_NAME.length; i++) {
                if(props.get(COLUMN_NAME[i]) != null){

                    // Put a single row into the table. We could also pass a list of Puts to write a batch.
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes(COLUMN_NAME[i]), Bytes.toBytes(props.get(COLUMN_NAME[i])));
                    table.put(put);
                }
            }
            // [END writing_rows]
        } catch (IOException e) {
            System.err.println("Exception while running trainsUpdate: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public final int trainsRemove(
            final Optional<TrainsModel> trainsOptional)
            throws Exception {
        ObjectMapper m = new ObjectMapper();
        Map<String,String> props = m.convertValue(trainsOptional.get(), Map.class);
        
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            // The admin API lets us create, manage and delete tables
            Admin admin = connection.getAdmin();
            // [END connecting_to_bigtable]

            // Retrieve the table we just created so we can do some reads and writes
            Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
            System.out.println("Table exist: " + admin.tableExists(TableName.valueOf(TABLE_NAME)));
            if(!admin.tableExists(TableName.valueOf(TABLE_NAME))){
                return 0;
            }
            
            // [START getting_a_row]
            // Get the row by row key
            String rowKey = trainsOptional.get().getNo();
            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
            if(getResult == Result.EMPTY_RESULT)
                return 0;
            // [END getting_a_row]
            
            Delete delete = new Delete(Bytes.toBytes(rowKey));
            for (int i = 0; i < COLUMN_NAME.length; i++) {
                if(props.get(COLUMN_NAME[i]) != null){
                    delete.addColumns(COLUMN_FAMILY_NAME, Bytes.toBytes(COLUMN_NAME[i])); // the 's' matters
                }
            }
            table.delete(delete);
            
        } catch (IOException e) {
            System.err.println("Exception while running trainsRemove: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 1;
    }

    @Override
    public final Optional<TrainsModel> trainsFind(
            final TrainsVo trainsVo)
            throws Exception {
        LOGGER.info("Start trainsFind");
        
        Boolean isFind = true;
        
        ObjectMapper m = new ObjectMapper();
        
        HashMap<String, String> result = new HashMap<String, String>();
        String rowKey = trainsVo.getNo();
        
        //建立回傳物件
        Optional<TrainsModel> trainsOptional
                = Optional.empty();
        
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            
            // The admin API lets us create, manage and delete tables
            Admin admin = connection.getAdmin();
            // [END connecting_to_bigtable]

            // Retrieve the table we just created so we can do some reads and writes
            Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
            System.out.println("Table exist: " + admin.tableExists(TableName.valueOf(TABLE_NAME)));
            if(!admin.tableExists(TableName.valueOf(TABLE_NAME))){
                return Optional.empty();
            }
            System.out.println("trainFind start getting a row");
            // [START getting_a_row]
            // Get the row by row key
            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
            if(getResult == Result.EMPTY_RESULT)
                return Optional.empty();
            else{
                System.out.println("Start set param");
                Map<String, String> paramMap = new HashMap<>();
                if(StringUtils.isNotBlank(trainsVo.getNo()))
                    paramMap.put("no", trainsVo.getNo());
                if(StringUtils.isNotBlank(trainsVo.getType()))
                    paramMap.put("type", trainsVo.getType());
                if(StringUtils.isNotBlank(trainsVo.getDate()))
                    paramMap.put("date", trainsVo.getDate());
                
                //設定參數
                for (Map.Entry<String, String> entry : paramMap.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    System.out.println("key:" + key);
                    System.out.println("value:" + key);
                    String column = Bytes.toString(getResult.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes(key)));
                    if(StringUtils.isNotBlank(column)){
                        if(!column.equals(value))
                            return Optional.empty();
                    }
                }
                
                for(int i = 0; i < COLUMN_NAME.length; i++){
                    byte[] columnValue = getResult.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes(COLUMN_NAME[i]));
                    if(columnValue != null)
                        result.put(COLUMN_NAME[i], Bytes.toString(columnValue));
                }
            }
            System.out.println("trainFind end getting a row");
            // [END getting_a_row]
        } catch (IOException e) {
            System.err.println("Exception while running trainsRemove: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Result size:" + result.size());
        if(result.size() > 0){
            result.put("no", rowKey);
            trainsOptional = Optional.ofNullable(m.convertValue(result, TrainsModel.class));
            System.out.println("Find result:" + result.toString());
        }
        LOGGER.info("End trainsFind");
        return trainsOptional;
    }

    @Override
    public final Optional<List<TrainsModel>> trainsFindList(
            final TrainsVo trainsVo)
            throws Exception {
        LOGGER.info("Start trainsFindList");
        
        Boolean isFind = true;
        
        ObjectMapper m = new ObjectMapper();
        
        HashMap<String, String> result = new HashMap<String, String>();
        String rowKey = trainsVo.getNo();
        
        //建立回傳物件
        Optional<List<TrainsModel>> trainsOptional
                = Optional.ofNullable(new ArrayList());
        List<TrainsModel> trainsModels = new ArrayList<>();
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            
            // The admin API lets us create, manage and delete tables
            Admin admin = connection.getAdmin();
            // [END connecting_to_bigtable]

            // Retrieve the table we just created so we can do some reads and writes
            Table table = connection.getTable(TableName.valueOf(TABLE_NAME));
            System.out.println("Table exist: " + admin.tableExists(TableName.valueOf(TABLE_NAME)));
            if(!admin.tableExists(TableName.valueOf(TABLE_NAME))){
                return Optional.empty();
            }
            System.out.println("trainFindList start getting rows");
            // [START getting_rows]
            // Get the rows
            List<Get> queryRowList = new ArrayList<Get>();
            if(StringUtils.isNotBlank(trainsVo.getType()))
                queryRowList.add(new Get(Bytes.toBytes("type:" + trainsVo.getType())));
            if(StringUtils.isNotBlank(trainsVo.getDate()))
                queryRowList.add(new Get(Bytes.toBytes("date:" + trainsVo.getDate())));
            System.out.println("Query " + queryRowList);
            Result[] results = table.get(queryRowList);
            System.out.println("Result size:" + results.length);
        
            for (Result r : results) {
                System.out.println(Bytes.toString(r.getRow()));
                result.put("no", Bytes.toString(r.getRow()));
                trainsModels.add(m.convertValue(result, TrainsModel.class));
            }
            System.out.println("trainFindList end getting rows");
            // [END getting_rows]
        } catch (IOException e) {
            System.err.println("Exception while running trainsRemove: " + e.getMessage());
            e.printStackTrace();
        }
        trainsOptional = Optional.ofNullable(trainsModels);
        LOGGER.info("End trainsFindList");
        return trainsOptional;
    }
}
