import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class MainGUI extends JFrame {
    private LocationDAO locationDAO;
    private PathDAO pathDAO;
    private Graph graph;
    private JLabel backgroundLabel;
    private JTextArea textArea;
    private JScrollPane scrollPane;
    private JTextArea inputTextArea;
    private JTextArea outputTextArea;
    private JButton sendButton;

    public MainGUI() {
        setTitle("校园导航系统系统");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        locationDAO = new LocationDAO();
        pathDAO = new PathDAO();

        try {
            int locationCount = locationDAO.getLocationCount();
            graph = new Graph(locationCount);

            List<Location> locations = locationDAO.getAllLocations();
            for (int i = 0; i < locations.size(); i++) {
                graph.addLocation(i, locations.get(i));
            }

            List<Path> paths = pathDAO.getAllPaths();
            for (Path path : paths) {
                graph.addEdge(path.getStartLocationId() - 1, path.getEndLocationId() - 1, path.getDistance());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "数据库初始化失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        initUI();
    }



    private void initUI() {
        setLayout(new BorderLayout());

        // 创建背景图片面板
        BackgroundPanel backgroundPanel = new BackgroundPanel("background.jpg");


        // 创建菜单栏
        JMenuBar menuBar = new JMenuBar();

        // 创建景点管理菜单
        JMenu locationMenu = new JMenu("景点管理");
        JMenuItem addItem = new JMenuItem("添加景点");
        JMenuItem deleteItem = new JMenuItem("删除景点");
        JMenuItem showLocationsItem = new JMenuItem("显示所有景点");

        locationMenu.add(addItem);
        locationMenu.add(deleteItem);
        locationMenu.add(showLocationsItem);

        // 创建道路管理菜单
        JMenu pathMenu = new JMenu("道路管理");
        JMenuItem addPathItem = new JMenuItem("添加道路");
        JMenuItem deletePathItem = new JMenuItem("删除道路");
        JMenuItem showPathsItem = new JMenuItem("显示所有道路");

        pathMenu.add(addPathItem);
        pathMenu.add(deletePathItem);
        pathMenu.add(showPathsItem);

        // 创建路径查询菜单
        JMenu queryMenu = new JMenu("路径查询");
        JMenuItem shortestPathItem = new JMenuItem("查询最短路径");
        JMenuItem restrictedPathItem = new JMenuItem("查询受限最短路径");

        queryMenu.add(shortestPathItem);
        queryMenu.add(restrictedPathItem);

        // 添加菜单到菜单栏
        menuBar.add(locationMenu);
        menuBar.add(pathMenu);
        menuBar.add(queryMenu);

        // 添加退出菜单
        JMenu exitMenu = new JMenu("退出");
        JMenuItem exitItem = new JMenuItem("退出程序");
        exitMenu.add(exitItem);
        menuBar.add(exitMenu);

        setJMenuBar(menuBar);

        // 添加事件监听器
        addItem.addActionListener(e -> addLocation());
        deleteItem.addActionListener(e -> deleteLocation());
        addPathItem.addActionListener(e -> addPath());
        deletePathItem.addActionListener(e -> deletePath());
        showLocationsItem.addActionListener(e -> showLocations());
        showPathsItem.addActionListener(e -> showPaths());
        shortestPathItem.addActionListener(e -> findShortestPath());
        restrictedPathItem.addActionListener(e -> findRestrictedPath());
        exitItem.addActionListener(e -> System.exit(0));

        // 创建文本区域用于显示信息
        outputTextArea = new JTextArea();
        outputTextArea.setEditable(true);
        outputTextArea.setLineWrap(true);  // 自动换行
        outputTextArea.setWrapStyleWord(true);  // 按单词边界换行
        JScrollPane outputScrollPane = new JScrollPane(outputTextArea);
        outputScrollPane.setPreferredSize(new Dimension(400, 100));  // 设置首选大小
        outputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  // 禁用水平滚动条


        inputTextArea = new JTextArea();
        inputTextArea.setEditable(false);  // 设置为不可编辑但可以显示
        inputTextArea.setLineWrap(true);  // 自动换行
        inputTextArea.setWrapStyleWord(true);  // 按单词边界换行
        JScrollPane inputScrollPane = new JScrollPane(inputTextArea);
        inputScrollPane.setPreferredSize(new Dimension(400, 200));  // 设置首选大小
        inputScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);  // 禁用水平滚动条
        inputTextArea.setText("您好！欢迎使用华东师范大学中北校区导航助手，我是您的智能导航伙伴【哈吉量\uD83D\uDE3A】。祝您在华东师范大学中北校区的行程顺利！\n");

        sendButton = new JButton("发送");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = outputTextArea.getText().trim(); // 获取输入并去除首尾空格

                if (message.isEmpty()) {
                    inputTextArea.append("【提示】请输入有效内容！\n");
                    return; // 如果输入为空，不发送请求
                }

                // 1. 回显用户输入（在 AI 回复之前显示）
                inputTextArea.append("【你】" + message + "\n");

                // 2. 发送请求并获取 AI 的回复
                String response = OpenAIClient.sendMessage(message);

                if (response != null) {
                    try {
                        // 3. 解析 JSON 并提取 AI 回复
                        JSONObject jsonResponse = new JSONObject(response);
                        JSONArray choices = jsonResponse.getJSONArray("choices");

                        if (choices.length() > 0) {
                            JSONObject firstChoice = choices.getJSONObject(0);
                            JSONObject messageObj = firstChoice.getJSONObject("message");
                            String aiResponse = messageObj.getString("content");

                            // 4. 显示 AI 的回复
                            inputTextArea.append("【哈吉量\uD83D\uDE3A】" + aiResponse + "\n");
                        } else {
                            inputTextArea.append("【错误】未获取到有效回复。\n");
                        }
                    } catch (JSONException ex) {
                        inputTextArea.append("【错误】解析回复失败：" + ex.getMessage() + "\n");
                        ex.printStackTrace();
                    }
                } else {
                    inputTextArea.append("【错误】未收到 API 响应。\n");
                }

                // 5. 清空输入框，方便用户输入下一条消息
                outputTextArea.setText("");

                // 6. 自动滚动到最新消息
                inputTextArea.setCaretPosition(inputTextArea.getDocument().getLength());
            }
        });

        // 创建右侧面板的布局
        JPanel rightPanel = new JPanel(new BorderLayout());

        // 添加输入显示区域在上部
        rightPanel.add(inputScrollPane, BorderLayout.CENTER);

        // 创建底部面板用于输出和发送按钮
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(outputScrollPane, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.SOUTH);

        // 将底部面板添加到右侧面板
        rightPanel.add(bottomPanel, BorderLayout.SOUTH);

        // 创建分割面板
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, backgroundPanel, rightPanel);
        splitPane.setDividerLocation(600);

        // 将分割面板添加到窗口中
        add(splitPane, BorderLayout.CENTER);
    }

    private void addLocation() {
        String name = JOptionPane.showInputDialog(this, "请输入景点名称：");
        String description = JOptionPane.showInputDialog(this, "请输入景点描述：");
        if (name != null && description != null) {
            try {
                locationDAO.addLocation(name, description);
                JOptionPane.showMessageDialog(this, "景点已成功添加。", "成功", JOptionPane.INFORMATION_MESSAGE);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "添加景点失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteLocation() {
        try {
            List<Location> locations = locationDAO.getAllLocations();
            String[] locationNames = locations.stream().map(Location::getName).toArray(String[]::new);
            String selectedName = (String) JOptionPane.showInputDialog(this, "请选择要删除的景点：", "删除景点", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);
            if (selectedName != null) {
                int locationId = locations.stream().filter(l -> l.getName().equals(selectedName)).findFirst().get().getId();
                locationDAO.deleteLocation(locationId);
                JOptionPane.showMessageDialog(this, "景点已成功删除。", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "删除景点失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addPath() {
        try {
            List<Location> locations = locationDAO.getAllLocations();
            String[] locationNames = locations.stream().map(Location::getName).toArray(String[]::new);

            String startName = (String) JOptionPane.showInputDialog(this, "请选择起点景点：", "添加道路", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);
            String endName = (String) JOptionPane.showInputDialog(this, "请选择终点景点：", "添加道路", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);
            String distanceStr = JOptionPane.showInputDialog(this, "请输入道路距离：");

            if (startName != null && endName != null && distanceStr != null) {
                int startId = locations.stream().filter(l -> l.getName().equals(startName)).findFirst().get().getId();
                int endId = locations.stream().filter(l -> l.getName().equals(endName)).findFirst().get().getId();
                int distance = Integer.parseInt(distanceStr);

                pathDAO.addPath(startId, endId, distance);
                JOptionPane.showMessageDialog(this, "道路已成功添加。", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "添加道路失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deletePath() {
        try {
            List<Path> paths = pathDAO.getAllPaths();
            String[] pathStrings = paths.stream()
                    .map(p -> p.getId() + ": " + p.getStartLocationId() + " -> " + p.getEndLocationId() + " (" + p.getDistance() + ")")
                    .toArray(String[]::new);

            String selectedPath = (String) JOptionPane.showInputDialog(this, "请选择要删除的道路：", "删除道路", JOptionPane.PLAIN_MESSAGE, null, pathStrings, pathStrings[0]);
            if (selectedPath != null) {
                int pathId = Integer.parseInt(selectedPath.split(":")[0].trim()); // 提取 pathId
                pathDAO.deletePath(pathId);
                JOptionPane.showMessageDialog(this, "道路已成功删除。", "成功", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "删除道路失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showLocations() {
        try {
            List<Location> locations = locationDAO.getAllLocations();
            StringBuilder sb = new StringBuilder("当前所有景点信息：\n");
            for (Location location : locations) {
                sb.append("ID: ").append(location.getId()).append(", 名称: ").append(location.getName()).append(", 描述: ").append(location.getDescription()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "景点信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "查询景点失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showPaths() {
        try {
            List<Path> paths = pathDAO.getAllPaths();
            StringBuilder sb = new StringBuilder("当前所有道路信息：\n");
            for (Path path : paths) {
                sb.append("起点ID: ").append(path.getStartLocationId()).append(", 终点ID: ").append(path.getEndLocationId()).append(", 距离: ").append(path.getDistance()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "道路信息", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "查询道路失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findShortestPath() {
        try {
            List<Location> locations = locationDAO.getAllLocations();
            String[] locationNames = locations.stream().map(Location::getName).toArray(String[]::new);

            String startName = (String) JOptionPane.showInputDialog(this, "请选择起点景点：", "查询最短路径", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);
            String endName = (String) JOptionPane.showInputDialog(this, "请选择终点景点：", "查询最短路径", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);

            if (startName != null && endName != null) {
                int startId = locations.stream().filter(l -> l.getName().equals(startName)).findFirst().get().getId();
                int endId = locations.stream().filter(l -> l.getName().equals(endName)).findFirst().get().getId();

                ShortestPathResult result = graph.dijkstra(startId - 1, endId - 1);
                if (result == null) {
                    JOptionPane.showMessageDialog(this, "没有找到从起点到终点的路径。", "结果", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    StringBuilder sb = new StringBuilder("最短路径：\n");
                    for (Location location : result.getPath()) {
                        sb.append(location.getName()).append(" ");
                    }
                    sb.append("\n路径长度：").append(result.getPathLength());
                    JOptionPane.showMessageDialog(this, sb.toString(), "最短路径结果", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "查询最短路径失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findRestrictedPath() {
        try {
            List<Location> locations = locationDAO.getAllLocations();
            String[] locationNames = locations.stream().map(Location::getName).toArray(String[]::new);

            String startName = (String) JOptionPane.showInputDialog(this, "请选择起点景点：", "查询最短路径", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);
            String endName = (String) JOptionPane.showInputDialog(this, "请选择终点景点：", "查询最短路径", JOptionPane.PLAIN_MESSAGE, null, locationNames, locationNames[0]);
            String numStr = JOptionPane.showInputDialog(this, "请输入经过的地点数（不包括起点）：");

            if (startName != null && endName != null && numStr != null) {
                int num = Integer.parseInt(numStr);

                Graph.PathResult result = graph.fixedVerticesPath(startName, endName, num);
                if (result == null) {
                    JOptionPane.showMessageDialog(this, "没有找到符合条件的受限路径。", "结果", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    List<Location> fixedPath = result.path;
                    int pathLength = result.length;

                    // 确保路径中包含终点
                    if (!fixedPath.isEmpty() && !fixedPath.get(fixedPath.size() - 1).getName().equals(endName)) {
                        fixedPath.add(locations.stream().filter(l -> l.getName().equals(endName)).findFirst().orElse(null));
                    }

                    StringBuilder sb = new StringBuilder("受限最短路径：\n");
                    for (Location location : fixedPath) {
                        if (location != null) {
                            sb.append(location.getName()).append(" -> ");
                        }
                    }
                    // 移除最后的" -> "
                    if (sb.length() > 4) {
                        sb.setLength(sb.length() - 4);
                    }

                    // 显示路径长度
                    sb.append("\n路径长度：").append(pathLength);

                    JOptionPane.showMessageDialog(this, sb.toString(), "受限路径结果", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "查询受限路径失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainGUI gui = new MainGUI();
            gui.setVisible(true);
        });
    }
}