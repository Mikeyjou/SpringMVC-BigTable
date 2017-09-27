/**
 * UsersDaoImpl
 * Author : Sian
 * Put Users Data to Database method
 */
package com.lavidatec.template.dao;

import com.lavidatec.template.entity.UsersModel;
import com.lavidatec.template.service.IUserService;
import com.lavidatec.template.service.UserServiceImpl;
import com.lavidatec.template.vo.UsersVo;
import java.io.IOException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;


import com.google.cloud.bigtable.hbase.BigtableConfiguration;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.codehaus.jackson.map.ObjectMapper;


/**
 * 2017/03/10 - Add UsersPersist.
 */
@Lazy
@Repository("IUsersDao")
public class UsersDaoImpl implements IUsersDao {

    /**
     * log init.
     */
    static final Logger LOGGER
            = LoggerFactory.getLogger(UsersDaoImpl.class);
    
    // Refer to table metadata names by byte array in the HBase API
    private static final byte[] TABLE_NAME = Bytes.toBytes("unit_users");
    private static final byte[] COLUMN_FAMILY_NAME = Bytes.toBytes("userInformation");
    private static final String[] COLUMN_NAME = {"password",
                                                    "identifier",
                                                    "orderList"};
    
    private static final String projectId = "plucky-lane-147516";
    private static final String instanceId = "quickstart-instance";
    
    @Override
    public final int usersInsert(
            final Optional<UsersModel> usersOptional)
            throws Exception {
        LOGGER.info("Start usersInsert");
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            
            ObjectMapper m = new ObjectMapper();
            Map<String,String> props = m.convertValue(usersOptional.get(), Map.class);
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
                    String rowKey = usersOptional.get().getAccount();

                    // Put a single row into the table. We could also pass a list of Puts to write a batch.
                    Put put = new Put(Bytes.toBytes(rowKey));
                    put.addColumn(COLUMN_FAMILY_NAME, Bytes.toBytes(COLUMN_NAME[i]), Bytes.toBytes(props.get(COLUMN_NAME[i])));
                    table.put(put);
                }
            }
            // [END writing_rows]

            // [START getting_a_row]
            // Get the first greeting by row key
//            String rowKey = usersOptional.get().getAccount();
//            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
//            String greeting = Bytes.toString(getResult.getValue(COLUMN_FAMILY_NAME, COLUMN_NAME));
//            System.out.println("Get a single greeting by row key");
//            System.out.printf("\t%s = %s\n", rowKey, greeting);
            // [END getting_a_row]

            // [START scanning_all_rows]
            // Now scan across all rows.
//            Scan scan = new Scan();
//
//            System.out.println("Scan for all greetings:");
//            ResultScanner scanner = table.getScanner(scan);
//            for (Result row : scanner) {
//                for(String column : COLUMN_NAME){
//                    byte[] valueBytes = row.getValue(COLUMN_FAMILY_NAME, Bytes.toBytes(column));
//                    System.out.println('\t' + Bytes.toString(valueBytes));
//                }
//            }
            // [END scanning_all_rows]
        } catch (IOException e) {
            System.err.println("Exception while running usersInsert: " + e.getMessage());
            e.printStackTrace();
        }
        LOGGER.info("End usersInsert");
        return 1;
    }
    
    @Override
    public final int usersUpdate(Optional<UsersModel> usersOptional)
            throws Exception{
        // [START connecting_to_bigtable]
        // Create the Bigtable connection, use try-with-resources to make sure it gets closed
        try (Connection connection = BigtableConfiguration.connect(projectId, instanceId)) {
            
            ObjectMapper m = new ObjectMapper();
            Map<String,String> props = m.convertValue(usersOptional.get(), Map.class);
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
            String rowKey = usersOptional.get().getAccount();
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
            System.err.println("Exception while running usersUpdate: " + e.getMessage());
            e.printStackTrace();
        }
        return 1;
    }
    
    @Override
    public final int usersRemove(Optional<UsersModel> usersOptional)
            throws Exception{
        
        ObjectMapper m = new ObjectMapper();
        Map<String,String> props = m.convertValue(usersOptional.get(), Map.class);
        
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
            String rowKey = usersOptional.get().getAccount();
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
            System.err.println("Exception while running usersRemove: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 1;
    }
    
    
    @Override
    public final Optional<UsersModel> usersFind(
            UsersVo usersVo)
            throws Exception{
        LOGGER.info("Start usersFind");
        
        Boolean isFind = true;
        
        ObjectMapper m = new ObjectMapper();
        
        HashMap<String, String> result = new HashMap<String, String>();
        String rowKey = usersVo.getAccount();
        
        //建立回傳物件
        Optional<UsersModel> usersOptional
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
            System.out.println("userFind start getting a row");
            // [START getting_a_row]
            // Get the row by row key
            Result getResult = table.get(new Get(Bytes.toBytes(rowKey)));
            if(getResult == Result.EMPTY_RESULT)
                return Optional.empty();
            else{
                System.out.println("Start set param");
                Map<String, String> paramMap = new HashMap<>();
                if(StringUtils.isNotBlank(usersVo.getAccount()))
                    paramMap.put("account", usersVo.getAccount());
                if(StringUtils.isNotBlank(usersVo.getPassword()))
                    paramMap.put("password", usersVo.getPassword());
                if(StringUtils.isNotBlank(usersVo.getIdentifier()))
                    paramMap.put("identifier", usersVo.getIdentifier());
                
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
            System.out.println("userFind end getting a row");
            // [END getting_a_row]
        } catch (IOException e) {
            System.err.println("Exception while running usersRemove: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Result size:" + result.size());
        if(result.size() > 0){
            result.put("account", rowKey);
            usersOptional = Optional.ofNullable(m.convertValue(result, UsersModel.class));
            System.out.println("Find result:" + result.toString());
        }
        LOGGER.info("End usersFind");
        return usersOptional;
    }
}