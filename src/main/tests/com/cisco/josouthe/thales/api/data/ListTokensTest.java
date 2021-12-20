package com.cisco.josouthe.thales.api.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import junit.framework.TestCase;

import java.util.Map;

public class ListTokensTest extends TestCase {
    private String testJSON="{\"skip\":0,\"limit\":10,\"total\":448,\"resources\":[{\"id\":\"777-952e-4110-b85f-1ed01a282ecb\",\"account\":\"xx:xx:admin:accounts:xx\",\"client_id\":\"xxx-fbed-4afd-af76-880349b089ba\",\"labels\":null,\"userId\":\"local|xxx-d955-42d8-961d-e1739085f63a\",\"username\":\"xxx\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-11-12T17:42:46.331871Z\",\"createdAt\":\"2021-11-12T17:42:46.33215Z\",\"updatedAt\":\"2021-11-12T17:42:46.33215Z\"},{\"id\":\"ba377bdd-7cbb-4eda-b282-91603167da3b\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"92771092-f2d0-4d2a-af07-580a69fd3d47\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-11-12T17:42:46.093825Z\",\"createdAt\":\"2021-11-12T17:42:46.0941Z\",\"updatedAt\":\"2021-11-12T17:42:46.0941Z\"},{\"id\":\"08069bc6-8c5b-413c-b0c4-9d7b2c5cde9e\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"00e25c48-672d-4f1e-b5f0-dad3e9751c7a\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-11-12T17:42:13.587516Z\",\"createdAt\":\"2021-11-12T17:42:13.587834Z\",\"updatedAt\":\"2021-11-12T17:42:13.587834Z\"},{\"id\":\"d0de9693-186c-4eeb-90ea-5ae38ffa73ff\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"72f2486e-cb0e-4b63-a457-8737dbb553e2\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-11-12T17:42:03.579659Z\",\"createdAt\":\"2021-11-12T17:42:03.580099Z\",\"updatedAt\":\"2021-11-12T17:42:03.580099Z\"},{\"id\":\"7204450d-558f-4148-bab6-d3064ecf215c\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"697d42b5-6635-4d98-a345-8efd05bed2fd\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-11-12T17:41:54.975413Z\",\"createdAt\":\"2021-11-12T17:41:54.975912Z\",\"updatedAt\":\"2021-11-12T17:41:54.975912Z\"},{\"id\":\"a84d14e8-9168-4741-b3e0-5979206323bb\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"99bfeece-296f-459f-a4da-1461184416fa\",\"labels\":[\"nae-auth\"],\"userId\":\"local|54c56b4a-9012-4281-a2e2-71190935a150\",\"username\":\"tokuser01\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":1440,\"revoked\":false,\"refreshedAt\":\"2021-11-11T18:18:04.407994Z\",\"createdAt\":\"2021-11-11T13:45:02.236867Z\",\"updatedAt\":\"2021-11-11T18:18:04.408254Z\"},{\"id\":\"9e49116d-a5e0-4a0a-809c-b4cc9cf4c1d9\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"46c126db-a4d2-452b-b5c3-1d24071ecfd7\",\"labels\":[\"nae-auth\"],\"userId\":\"local|54c56b4a-9012-4281-a2e2-71190935a150\",\"username\":\"tokuser01\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":1440,\"revoked\":false,\"refreshedAt\":\"2021-11-10T17:57:03.271537Z\",\"createdAt\":\"2021-11-10T08:13:51.579773Z\",\"updatedAt\":\"2021-11-10T17:57:03.271863Z\"},{\"id\":\"cc151219-29f1-4a18-bf92-18e4331e399c\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"496cc23c-b26f-4b23-8188-539229875f59\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-10-28T19:06:06.380757Z\",\"createdAt\":\"2021-10-28T19:06:06.380996Z\",\"updatedAt\":\"2021-10-28T19:06:06.380996Z\"},{\"id\":\"5e33b083-1899-474c-a22c-3274160bad81\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"6115e942-5f08-4934-a468-ea42e815ad40\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-10-28T19:06:06.189912Z\",\"createdAt\":\"2021-10-28T19:06:06.190172Z\",\"updatedAt\":\"2021-10-28T19:06:06.190172Z\"},{\"id\":\"3c1b337f-2681-44aa-a52e-7d082bb57ee6\",\"account\":\"xxx:xxx:admin:accounts:xxx\",\"client_id\":\"8aabfe85-68a6-48d5-88f1-6ba0d75fe78a\",\"labels\":null,\"userId\":\"local|101383fc-d955-42d8-961d-e1739085f63a\",\"username\":\"someuser\",\"expiresIn\":0,\"expired\":false,\"revokeNotRefreshedIn\":0,\"revoked\":false,\"refreshedAt\":\"2021-10-28T19:06:05.953892Z\",\"createdAt\":\"2021-10-28T19:06:05.954126Z\",\"updatedAt\":\"2021-10-28T19:06:05.954126Z\"}]}\n";
    ListTokens listTokens;

    public void testGsonParserCreation() throws Exception {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.listTokens = gson.fromJson(testJSON, ListTokens.class);
        System.out.println("# records: "+ listTokens.resources.size());
        assert listTokens.total == 448;
        assert listTokens.hasMore() == true;
        assert listTokens.resources.size() == 10;

        for( Token token : listTokens.resources)
            System.out.println("Token ID: "+ token.id);

        Map<String,Integer> tokensMap = listTokens.getTokensCountsByStatus();
        for( String state : tokensMap.keySet() )
            System.out.println("Tokens "+ state+" : "+tokensMap.get(state) );
    }


}