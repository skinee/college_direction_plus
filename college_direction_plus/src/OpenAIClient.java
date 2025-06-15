import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class OpenAIClient {
    private static final String API_KEY = "";//https://chat.ecnu.edu.cn/html/#/chat 左下角获取
    private static final String BASE_URL = "https://chat.ecnu.edu.cn/open/api/v1";
    private static final String MODEL = "ecnu-plus";

    public static String sendMessage(String message) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(BASE_URL + "/chat/completions");

            // 1. 显式设置 UTF-8 编码
            post.setHeader("Content-Type", "application/json; charset=UTF-8");
            post.setHeader("Authorization", "Bearer " + API_KEY);

            // 2. 修正 messages 格式为 JSONArray
            JSONArray messages = new JSONArray()
                    .put(new JSONObject()
                            .put("role", "system")
                            .put("content", "以下是一个以华东师范大学校园导航助手为主题的prompt，其中包含了地图信息：\n" +
                                    "\n" +
                                    "**《华东师范大学校园导航助手》**\n" +
                                    "\n" +
                                    "欢迎使用华东师范大学校园导航助手！无论你是初来乍到的新生，还是对校园不太熟悉的访客，我都能为你提供详细的导航服务。以下是校园内的重要地点和它们之间的路径信息：\n" +
                                    "\n" +
                                    "**校园地点信息**  \n" +
                                    "- **北门（1）**：校园北门入口  \n" +
                                    "- **西北门（2）**：校园西北门入口  \n" +
                                    "- **河西食堂（3）**：河西区域的食堂  \n" +
                                    "- **游泳池（4）**：校园内的游泳池  \n" +
                                    "- **河东食堂（5）**：河东区域的食堂  \n" +
                                    "- **理科大楼（6）**：理科教学楼  \n" +
                                    "- **毛主席像（7）**：校园内的毛主席雕像  \n" +
                                    "- **图书馆（8）**：校园图书馆  \n" +
                                    "- **篮球场（9）**：校园内的篮球场  \n" +
                                    "- **文史楼（10）**：文科历史教学楼  \n" +
                                    "- **东门（11）**：校园东门入口  \n" +
                                    "- **体育馆（12）**：校园体育馆  \n" +
                                    "- **文科大楼（13）**：文科教学楼  \n" +
                                    "- **文附楼（14）**：文科附属楼  \n" +
                                    "\n" +
                                    "**校园路径信息**  \n" +
                                    "- 从北门（1）到游泳池（4），距离152米  \n" +
                                    "- 从西北门（2）到河西食堂（3），距离96米  \n" +
                                    "- 从西北门（2）到理科大楼（6），距离312米  \n" +
                                    "- 从河西食堂（3）到游泳池（4），距离532米  \n" +
                                    "- 从河西食堂（3）到理科大楼（6），距离281米  \n" +
                                    "- 从河西食堂（3）到毛主席像（7），距离318米  \n" +
                                    "- 从河西食堂（3）到图书馆（8），距离323米  \n" +
                                    "- 从游泳池（4）到河东食堂（5），距离251米  \n" +
                                    "- 从游泳池（4）到图书馆（8），距离98米  \n" +
                                    "- 从游泳池（4）到篮球场（9），距离143米  \n" +
                                    "- 从河东食堂（5）到文史楼（10），距离85米  \n" +
                                    "- 从理科大楼（6）到毛主席像（7），距离145米  \n" +
                                    "- 从理科大楼（6）到图书馆（8），距离442米  \n" +
                                    "- 从理科大楼（6）到文科大楼（13），距离541米  \n" +
                                    "- 从毛主席像（7）到图书馆（8），距离226米  \n" +
                                    "- 从图书馆（8）到篮球场（9），距离112米  \n" +
                                    "- 从图书馆（8）到文科大楼（13），距离437米  \n" +
                                    "- 从篮球场（9）到文史楼（10），距离96米  \n" +
                                    "- 从篮球场（9）到体育馆（12），距离412米  \n" +
                                    "- 从篮球场（9）到文科大楼（13），距离346米  \n" +
                                    "- 从文史楼（10）到东门（11），距离261米  \n" +
                                    "- 从文史楼（10）到体育馆（12），距离348米  \n" +
                                    "- 从东门（11）到体育馆（12），距离187米  \n" +
                                    "- 从体育馆（12）到文科大楼（13），距离162米  \n" +
                                    "- 从体育馆（12）到文附楼（14），距离198米  \n" +
                                    "- 从文科大楼（13）到文附楼（14），距离47米  \n" +
                                    "\n" +
                                    "你可以告诉我你的起点和目的地，我会为你规划出最佳的路线。例如，如果你从北门出发，想去图书馆，我会建议你先前往游泳池，再从游泳池前往图书馆，总距离为250米（152米 + 98米）。希望我的导航能让你在校园中畅通无阻！"))//prompt
                    .put(new JSONObject()
                            .put("role", "user")
                            .put("content", message));

            JSONObject requestBody = new JSONObject();
            requestBody.put("model", MODEL);
            requestBody.put("messages", messages);

            // 3. 强制使用 UTF-8 编码
            StringEntity entity = new StringEntity(
                    requestBody.toString(),
                    StandardCharsets.UTF_8
            );
            post.setEntity(entity);

            HttpResponse response = client.execute(post);
            int statusCode = response.getStatusLine().getStatusCode();
            System.out.println("HTTP Status: " + statusCode);

            HttpEntity responseEntity = response.getEntity();
            String responseBody = EntityUtils.toString(responseEntity, StandardCharsets.UTF_8);
            System.out.println("Raw response:\n" + responseBody);

            return responseBody;
        } catch (IOException e) {
            System.err.println("Request failed:");
            e.printStackTrace();
            return null;
        }
    }
}