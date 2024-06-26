/*
 * Created by JFormDesigner on Mon May 27 11:00:12 CST 2024
 */
package cn.qht2005.www.view.gui.main;

import java.awt.*;
import java.awt.event.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import cn.qht2005.www.pojo.College;
import cn.qht2005.www.pojo.Course;
import cn.qht2005.www.pojo.Enumeration.UserType;
import cn.qht2005.www.pojo.Notice;
import cn.qht2005.www.pojo.people.Student;
import cn.qht2005.www.pojo.people.Teacher;
import cn.qht2005.www.service.impl.AdministratorServiceImpl;
import cn.qht2005.www.service.impl.CollegeServiceImpl;
import cn.qht2005.www.service.impl.TeacherServiceImpl;
import cn.qht2005.www.view.gui.*;
import com.formdev.flatlaf.FlatLightLaf;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.general.DefaultPieDataset;

/**
 * @author 覃
 */
public class AdministratorControllerGui extends JFrame {
    private List<Student> students = null;
    private List<Teacher> teachers = null;
    public AdministratorControllerGui() {
        initComponents();
        init();
    }
    {
        try {
            // 获取所有学生对象
            students = new TeacherServiceImpl().getAllStudent();
            // 获取所有的教职工对象
            teachers = new TeacherServiceImpl().getAllTeacher();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 那啥文件夹改变
    private void tabbedPaneMenuStateChanged(ChangeEvent e) {
        if (tabbedPaneMenu.getSelectedIndex() == 0) {
            // 总览
            showMain();
        } else if (tabbedPaneMenu.getSelectedIndex() == 1) {
            // 学生管理
            try {
                // 展示所有学生列表到表格上
                showStudentInfoToTable();
                // 展示所有
                showStudentCountByCollegeToChart();
                showCollegeListToSelectBox(selectBoxStudentCollege);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        } else if (tabbedPaneMenu.getSelectedIndex() == 2) {
            // 教职工管理
            try {
                showTeacherAllToTable();
                showCollegeListToSelectBox(selectBoxTeacherCollege);
                showTeacherCountByPositionToChart();

            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }


        } else if (tabbedPaneMenu.getSelectedIndex() == 3) {
            // 学院管理
            try {
                showCollegeInfoToTable();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }else if (tabbedPaneMenu.getSelectedIndex() == 4) {
            // 课程管理
            showCourseListToTable(null);
            showCollegeListToSelectBox(selectCourseCollege);
        } else if (tabbedPaneMenu.getSelectedIndex() == 5) {
            // 通知管理
            showNoticeToTable(null);

        }


    }
    // 展示所有公告到公列表上
    private void showNoticeToTable(List<Notice> notices) {
        try {
            // 获取所有公告
            if (notices == null){
                notices = new AdministratorServiceImpl().getAllNotice();
            }
            // 将所有公告展示到表格上
            DefaultTableModel model = (DefaultTableModel) tableNoticeList.getModel();
            model.setRowCount(0);
            for (Notice notice : notices) {
                model.addRow(new Object[]{
                        // 获取公告id
                        notice.getNoticeId(),
                        // 获取发文机关
                        notice.getPublisher(),
                        // 获取主送机关 0.所有 1.学生 2.教师
                        notice.getRecipient() == 0 ? "全体师生" : notice.getRecipient() == 1 ? "全体学生" : "全体教师",
                        // 获取发文时间
                        notice.getPublishDate(),
                        // 获取公告标题
                        notice.getTitle(),
                        // 获取公告内容
                        notice.getBody()
                });
            }
            // 设置表格内容居中
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(JLabel.CENTER);
            tableNoticeList.setDefaultRenderer(Object.class, r);
//            tableNoticeList.setDefaultRenderer(Integer.class, r);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 将所有教职工列表展示到那玩意的表格上
    private void showTeacherAllToTable() throws Exception {
        DefaultTableModel model = (DefaultTableModel) tableTeacherList.getModel();
        model.setRowCount(0);
        for (Teacher teacher : teachers) {
            model.addRow(new Object[]{
                    //  工号
                    teacher.getTeacherId(),
                    new CollegeServiceImpl().getCollegeNameById(teacher.getCollegeId()),
                    teacher.getMangeClassId(),
                    teacher.getName(),
                    teacher.getAge(),
                    teacher.getSex() == 1 ? "男" : "女",
                    teacher.getPosition(),
                    teacher.getPhoneNumber(),
                    teacher.getPassWord(),
            });
            // 设置表格内容居中
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(JLabel.CENTER);
            tableTeacherList.setDefaultRenderer(Object.class, r);
            tableTeacherList.setDefaultRenderer(Integer.class, r);
        }
        // 展示总人数到那啥标签上
        labelForTeacherCount.setText(model.getRowCount() + "");
    }
    // 展示学院列表到选择框上 形参：一个选择框
    private void showCollegeListToSelectBox(JComboBox<String> selectBox) {
        try {
            selectBox.removeAllItems();
            selectBox.addItem("不限");
            new CollegeServiceImpl().getAllCollege().forEach(college -> selectBox.addItem(college.getCollegeName()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 展示各学院人数并展示到图表上
    private void showStudentCountByCollegeToChart() {
        try {
            // 获取学院列表
            List<College> colleges = new CollegeServiceImpl().getAllCollege();
            // 获取各学院人数
            Map<String, Integer> studentCountByCollege = new AdministratorServiceImpl().getStudentCountByCollege();
            // 将其可视化
            DefaultPieDataset dataset = new DefaultPieDataset();
            for (College college : colleges) {
                dataset.setValue(college.getCollegeName(), studentCountByCollege.get(college.getCollegeName()));
            }
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "studentCountByCollege", // 图标题
                    dataset, // 数据集
                    true, true, true);
            PiePlot plot = (PiePlot) pieChart.getPlot();
            // 关闭图表文字抗锯齿
            pieChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            ChartPanel chartPanel = new ChartPanel(pieChart);
            // 为那些玩意设置中文字体，不然不显示
            Font LegendFont = new Font("宋体", Font.PLAIN, 15);
            LegendTitle legend = pieChart.getLegend(0);
            legend.setItemFont(LegendFont);
            plot.setLabelFont(new Font("黑体", Font.PLAIN, 13));
            // 设置那啥位置，让其能够显示
            chartPanel.setBounds(0, 0, panelCountStudentByCollege.getWidth(), panelCountStudentByCollege.getHeight());
            // 刷新一下组件
            panelCountStudentByCollege.add(chartPanel);
            panelCountStudentByCollege.revalidate();
            panelCountStudentByCollege.repaint();


        } catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    // 展示各职位教师到图表上
    private void showTeacherCountByPositionToChart() {
        try {
            // 获取各职位人数
            Map<String, Integer> teacherCountByPosition = new AdministratorServiceImpl().getTeacherCountByPosition(teachers);
            // 将其可视化
            DefaultPieDataset dataset = new DefaultPieDataset();

                teacherCountByPosition.forEach((position, count) -> {
                    if (position == null) {
                        dataset.setValue("其他", count);
                    }else{
                        dataset.setValue(position, count);
                    }
                });

            JFreeChart pieChart = ChartFactory.createPieChart(
                    "teacherCountByPosition", // 图标题
                    dataset, // 数据集
                    true, true, true);
            PiePlot plot = (PiePlot) pieChart.getPlot();
            // 关闭图表文字抗锯齿
            pieChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            ChartPanel chartPanel = new ChartPanel(pieChart);
            // 为那些玩意设置中文字体，不然不显示
            Font LegendFont = new Font("宋体", Font.PLAIN, 15);
            LegendTitle legend = pieChart.getLegend(0);
            legend.setItemFont(LegendFont);
            plot.setLabelFont(new Font("黑体", Font.PLAIN, 13));
            // 设置那啥位置，让其能够显示
            chartPanel.setBounds(0, 0, panelCountTeacherByPositon.getWidth(), panelCountTeacherByPositon.getHeight());
            // 刷新一下组件
            panelCountTeacherByPositon.add(chartPanel);
            panelCountTeacherByPositon.revalidate();
            panelCountTeacherByPositon.repaint();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 自定义的初始化
    private void init()  {
        try {
            // 默认展示总览
            if (tabbedPaneMenu.getSelectedIndex() == 0)showMain();
            // 将学生总人数展示到总览的标签上
            labelStudentCount.setText(students.size() + "");
            // 将教职工总人数展示到总览的标签上
            labelTeacherCount.setText(teachers.size() + "");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    // 总览
    private void showMain() {
        // 展示学生男女人数
        showStudentSexCountToChart();
        // 展示教师各年龄段人数
        showTeacherAgeCountToChart();
    }
    // 将学生男女人数显示到图表
    private void showStudentSexCountToChart(){
        try {
            // 创建服务对象
            TeacherServiceImpl service = new TeacherServiceImpl();
            // 获取男女人数
            Map<String, Integer> studentCountBySex = service.getStudentCountBySex(students);
            // 将其可视化
            DefaultPieDataset dataset = new DefaultPieDataset();
            dataset.setValue("boy", studentCountBySex.get("男"));
            dataset.setValue("girl", studentCountBySex.get("女"));
            JFreeChart pieChart = ChartFactory.createPieChart(
                    "studentCountBySex", // 图标题
                    dataset, // 数据集
                    true, true, true);
            PiePlot plot = (PiePlot) pieChart.getPlot();
            // 关闭图表文字抗锯齿
            pieChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
/*             设置标签生成器
             "{0}" 表示 section name
             "{1}" 表示 section value
             "{2}" 表示 Percent*/
            plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}-{2}"));
            ChartPanel chartPanel = new ChartPanel(pieChart);
            chartPanel.setBounds(0, 0, panelForStudentSexCount.getWidth(), panelForStudentSexCount.getHeight());
            panelForStudentSexCount.add(chartPanel);
            panelForStudentSexCount.revalidate();
            panelForStudentSexCount.repaint();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    // 将各年龄段教师人数展示到图表
    private void showTeacherAgeCountToChart(){
        Map<String, Integer> teacherCountByAge = new AdministratorServiceImpl().getTeacherCountByAge();
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("under35", teacherCountByAge.get("under35"));
        dataset.setValue("35to50", teacherCountByAge.get("35to50"));
        dataset.setValue("above50", teacherCountByAge.get("above50"));
        JFreeChart pieChart = ChartFactory.createPieChart(
                "teacherCountByAge", // 图标题
                dataset, // 数据集
                true, true, true);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}-{2}"));
        // 关闭图表文字抗锯齿
        pieChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
/*             设置标签生成器
             "{0}" 表示 section name
             "{1}" 表示 section value
             "{2}" 表示 Percent*/
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}-{2}"));
        ChartPanel chartPanel = new ChartPanel(pieChart);
        chartPanel.setBounds(0, 0, panelForTeacherAgeCount.getWidth(), panelForTeacherAgeCount.getHeight());
        panelForTeacherAgeCount.add(chartPanel);
        panelForTeacherAgeCount.revalidate();
        panelForTeacherAgeCount.repaint();

    }

    // 窗口将要被关闭
    private void thisWindowClosing(WindowEvent e) {
        //确认对话框
        int option = JOptionPane.showConfirmDialog(null, "狗子，你真的要离开我么？", "翠花含情脉脉",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (option == JOptionPane.YES_OPTION){
            // 关闭程序
            this.dispose();
        }
    }
    // 查询按钮被点击
    private void buttonQueryMouseClicked(MouseEvent e) {
        try {
            queryStudent();
            showStudentInfoToTable();
            showStudentCountByCollegeToChart();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // 动态条件查询学生
    private void queryStudent() throws Exception {
        // 姓名
        String name = inputStudentName.getText();
        // 学号
        String id = inputStudentId.getText();
        // 性别
        Integer sex = selectBoxStudentSex.getSelectedIndex() == 0 ? null : selectBoxStudentSex.getSelectedIndex() == 1 ? 1 : 2;
        // 学院
        String collegeName = Objects.equals(Objects.requireNonNull(selectBoxStudentCollege.getSelectedItem())
                .toString(), "不限") ? null : selectBoxStudentCollege.getSelectedItem().toString();
        // 创建学生对象
        Student student = new Student();
        student.setName(name);
        student.setStudentId(id);
        if (collegeName != null) {
            student.setCollegeId(new CollegeServiceImpl().getCollegeIdByName(collegeName));
        }
        if (sex != null) {
            student.setSex(sex.shortValue());
        }
        // 将查询到的学生列表赋值给全局变量students
        students = new TeacherServiceImpl().getStudentByDynamic(student);
    }
    // 查询教师按钮被点击
    private void buttonQueryForTeacherMouseClicked(MouseEvent e) {
        try {
            queryTeacher();
            showTeacherAllToTable();
            showTeacherCountByPositionToChart();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // 查询教师然后展示到表格上
    private void queryTeacher() {
        try {
            // 教师姓名
            String name = inputTeacherName.getText();
            // 教师工号
            String id = inputTeacherId.getText();
            // 教师性别
            Integer sex = selectBoxTeacherSex.getSelectedIndex() == 0 ? null : selectBoxTeacherSex.getSelectedIndex() == 1 ? 1 : 2;
            // 教师学院
            String collegeName = Objects.equals(Objects.requireNonNull(selectBoxTeacherCollege.getSelectedItem())
                    .toString(), "不限") ? null : selectBoxTeacherCollege.getSelectedItem().toString();
            // 创建一个教师对象
            Teacher teacher = new Teacher();
            // 对这个教师对象进行赋值
            teacher.setName(name);
            teacher.setTeacherId(id);
            if (collegeName != null) {
                teacher.setCollegeId(new CollegeServiceImpl().getCollegeIdByName(collegeName));
            }
            teacher.setSex(sex == null ? null: sex == 1 ? (short)1 : (short)2);
            // 进行动态条件查询
            teachers = new TeacherServiceImpl().getTeacherByDynamic(teacher);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    // 添加教师按钮被点击
    private void buttonAddTeacherMouseClicked(MouseEvent e) {
        // 创建一个添加用户信息的窗口
        AddUser addUser = new AddUser(this, UserType.TEACHER);
        // 显示为显示
        addUser.setVisible(true);
        addUser.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                // 刷新一下
                try {
                    teachers = new TeacherServiceImpl().getAllTeacher();
                    showTeacherAllToTable();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                showTeacherCountByPositionToChart();
            }
        });


    }
    // 添加学生按钮被点击
    private void buttonAddStudentMouseClicked(MouseEvent e) {
        // 创建一个窗口
        AddUser addUser = new AddUser(this, UserType.STUDENT);
        // 设置为可见
        addUser.setVisible(true);
        // 刷新一下
        addUser.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    students = new TeacherServiceImpl().getAllStudent();
                    showStudentInfoToTable();
                    showStudentCountByCollegeToChart();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }
    // 发布公告按钮被点击
    private void buttonPublishNoticeMouseClicked(MouseEvent e) {
        AddNotice addNotice = new AddNotice(this);
        addNotice.setVisible(true);
        addNotice.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                showNoticeToTable(null);
            }
        });
    }
    // 查询公告按钮被点击
    private void buttonQueryNoticeMouseClicked(MouseEvent e) {
        // 获取查询到的公告
        List<Notice> notices = queryNotice();
        // 展示到表格上
        showNoticeToTable(notices);
    }
    // 根据条件查询公告
    private List<Notice> queryNotice() {
        // 获取文本框的公告id
        String noticeId = inputNoticeId.getText();
        // 获取文本框的公告内容包含
        String contentContain = inputNoticeContain.getText();
        // 创建一个公告对象
        Notice notice = new Notice();
        // 对这个公告对象进行赋值
        if (!noticeId.isEmpty()) {
            notice.setNoticeId(Integer.parseInt(noticeId));
        }
        notice.setBody(contentContain);
        // 获取结果并返回
        return new AdministratorServiceImpl().getNoticeByNotice(notice);
    }
    // 删除公告按钮被点击
    private void buttonDeleteNoticeMouseClicked(MouseEvent e) {
        deleteNotice();
        buttonQueryNoticeMouseClicked(null);
    }
    // 删除公告
    private void deleteNotice(){
        // 获取选中的行
        int[] rows = tableNoticeList.getSelectedRows();
        if (rows.length == 0){
            JOptionPane.showMessageDialog(null, "请选择要删除的公告");
            return;
        }
        // 创建一个公告列表
        List<Notice> notices = new ArrayList<>();
        // 遍历选中的行
        for (int row : rows) {
            // 创建一个公告对象
            Notice notice = new Notice();
            // 对这个公告对象进行赋值
            notice.setNoticeId((Integer) tableNoticeList.getValueAt(row, 0));
            // 将这个公告对象添加到公告列表中
            notices.add(notice);
        }
        // 删除公告
        boolean result = new AdministratorServiceImpl().deleteNotices(notices);
        // 判断是否删除成功
        if (result) {
            JOptionPane.showMessageDialog(null, "删除成功");
            // 刷新
            showNoticeToTable(null);
        } else {
            JOptionPane.showMessageDialog(null, "删除失败");
        }
    }
    // 删除学生按钮被点击
    private void buttonDeleteStudentMouseClicked(MouseEvent e) {
        try {
            deleteStudent();
            // 刷新一下
            students = new TeacherServiceImpl().getAllStudent();
            showStudentInfoToTable();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
    // 删除学生
    private void deleteStudent() throws Exception {
        // 获取选中的行
        int[] rows = tableStudentList.getSelectedRows();
        if (rows.length == 0){
            JOptionPane.showMessageDialog(null, "请选择要删除的学生");
            return;
        }
        // 创建一个学生列表
        List<Student> students = new ArrayList<>();
        // 遍历选中的行
        for (int row : rows) {
            // 创建一个学生对象
            Student student = new Student();
            // 对这个学生对象进行赋值
            student.setStudentId((String) tableStudentList.getValueAt(row, 0));
            // 将这个学生对象添加到学生列表中
            students.add(student);
        }
        // 删除学生
        boolean result = new AdministratorServiceImpl().deleteByStudentList(students);
        // 判断是否删除成功
        if (result) {
            JOptionPane.showMessageDialog(null, "删除成功");
            // 刷新
            showStudentInfoToTable();
            showStudentCountByCollegeToChart();
        } else {
            JOptionPane.showMessageDialog(null, "删除失败");
        }

    }
    // 删除教师按钮被点击
    private void buttonDeleteStudent2MouseClicked(MouseEvent e) {
        try {
            deleteTeacher();
            // 刷新一下
            teachers = new TeacherServiceImpl().getAllTeacher();
            showTeacherAllToTable();

        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // 删除教师
    private void deleteTeacher(){
        // 获取选中的行
        int[] rows = tableTeacherList.getSelectedRows();
        if (rows.length == 0){
            JOptionPane.showMessageDialog(null, "请选择要删除的教师");
            return;
        }
        // 创建一个教师列表
        List<Teacher> teachers = new ArrayList<>();
        // 遍历选中的行
        for (int row : rows) {
            // 创建一个教师对象
            Teacher teacher = new Teacher();
            // 对这个教师对象进行赋值
            teacher.setTeacherId((String) tableTeacherList.getValueAt(row, 0));
            // 将这个教师对象添加到教师列表中
            teachers.add(teacher);
        }
        // 删除教师
        boolean result = new AdministratorServiceImpl().deleteByTeacherList(teachers);
        // 判断是否删除成功
        if (result) {
            JOptionPane.showMessageDialog(null, "删除成功");
        } else {
            JOptionPane.showMessageDialog(null, "删除失败");
        }

    }
    // 导出学生按钮被点击
    private void buttonExportStudentMouseClicked(MouseEvent e) {
        // 导出学生列表
        exportStudent();
    }
    // 导出学生列表
    private void exportStudent() {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("学生列表");
        Row row = sheet.createRow(0);
        for (int i = 0; i < tableStudentList.getColumnCount(); i++) {
            row.createCell(i).setCellValue(tableStudentList.getColumnName(i));
        }
        for (int i = 0; i < tableStudentList.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < tableStudentList.getColumnCount(); j++) {
                if (tableStudentList.getValueAt(i, j) != null){
                    row.createCell(j).setCellValue(tableStudentList.getValueAt(i, j).toString());
                }
            }
        }
        // 导出
        // 创建一个文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置文件选择器的标题
        fileChooser.setDialogTitle("请选择导出路径");
        // 设置文件选择器的默认路径
        fileChooser.setCurrentDirectory(new java.io.File("."));
        // 设置文件选择器的选择模式
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // 打开文件选择器
        int result = fileChooser.showSaveDialog(null);
        // 判断是否点击了保存按钮
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取选择的文件夹
            try (FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile() + ".xlsx")) {
                workbook.write(fos);
                JOptionPane.showMessageDialog(null, "导出成功");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "导出失败");
                throw new RuntimeException(e);
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // 导出教师列表按钮被点击
    private void buttonExportForTeacherMouseClicked(MouseEvent e) {
        exportTeacher();
    }
    // 导出教师列表
    private void exportTeacher(){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("教师列表");
        Row row = sheet.createRow(0);
        for (int i = 0; i < tableTeacherList.getColumnCount(); i++) {
            row.createCell(i).setCellValue(tableTeacherList.getColumnName(i));
        }
        for (int i = 0; i < tableTeacherList.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < tableTeacherList.getColumnCount(); j++) {
                String value = "";
                if (tableTeacherList.getValueAt(i, j) != null) {
                    row.createCell(j).setCellValue(tableTeacherList.getValueAt(i, j).toString());
                }
            }
        }
        // 导出
        // 创建一个文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置文件选择器的标题
        fileChooser.setDialogTitle("请选择导出路径");
        // 设置文件选择器的默认路径
        fileChooser.setCurrentDirectory(new java.io.File("."));
        // 设置文件选择器的选择模式
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // 打开文件选择器
        int result = fileChooser.showSaveDialog(null);
        // 判断是否点击了保存按钮
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取选择的文件夹
            try (FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile() + ".xlsx")) {
                workbook.write(fos);
                JOptionPane.showMessageDialog(null, "导出成功");
                workbook.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "导出失败");
                throw new RuntimeException(e);
            }
        }
    }
    // 修改学生信息按钮被点击
    private void buttonUpdateStudentMouseClicked(MouseEvent e) {
        // 获取当前选中的行
        int row = tableStudentList.getSelectedRow();
        // 判断是否选中
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "请选择要修改的学生");
            return;
        }
        // 获取学生id
        String studentId = (String) tableStudentList.getValueAt(row, 0);
        // 获取学生对象
        Student student = null;
        try {
            student = new TeacherServiceImpl().getStudentById(studentId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        UpdateStudentAndTeacher updateStudentAndTeacher = new UpdateStudentAndTeacher(this, student);
        updateStudentAndTeacher.setVisible(true);
        // 刷新一下
        updateStudentAndTeacher.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    students = new TeacherServiceImpl().getAllStudent();
                    showStudentInfoToTable();
                    showStudentCountByCollegeToChart();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }
    // 修改教师信息按钮被点击
    private void buttonUpdateTeacherMouseClicked(MouseEvent e) {
        // 获取当前选中的行
        int row = tableTeacherList.getSelectedRow();
        // 判断是否选中
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "请选择要修改的教师");
            return;
        }
        // 获取教师id
        String teacherId = (String) tableTeacherList.getValueAt(row, 0);
        // 获取教师对象
        Teacher teacher = null;
        try {
            teacher = new TeacherServiceImpl().getTeacherById(teacherId);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        // 创建一个窗口
        UpdateStudentAndTeacher updateStudentAndTeacher = new UpdateStudentAndTeacher(this, teacher);
        // 设置为显示
        updateStudentAndTeacher.setVisible(true);
        // 刷新一下
        updateStudentAndTeacher.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                try {
                    teachers = new TeacherServiceImpl().getAllTeacher();
                    showTeacherAllToTable();
                    showTeacherCountByPositionToChart();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

    }
    // 获取课程列表并展示到表格上
    private void showCourseListToTable(List<Course> courses){
        // 给一个默认值
        try {
            if (courses == null){
                courses = new AdministratorServiceImpl().getAllCourse();
            }
            DefaultTableModel model = (DefaultTableModel) tableCourseList.getModel();
            model.setRowCount(0);
            for (Course course : courses) {
                model.addRow(new Object[]{
                        course.getCourseId(),
                        course.getCourseName(),
                        new CollegeServiceImpl().getCollegeNameById(course.getCollegeId()),
                });
            }
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(JLabel.CENTER);
            tableCourseList.setDefaultRenderer(Object.class, r);
            tableCourseList.setDefaultRenderer(Integer.class, r);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 添加课程按钮被点击
    private void buttonAddCourseMouseClicked(MouseEvent e) {
        // 创建一个窗口
        AddAndUpdateCourse addAndUpdateCourse = new AddAndUpdateCourse(this, null);
        // 设置为可见
        addAndUpdateCourse.setVisible(true);
        // 刷新一下
        addAndUpdateCourse.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                showCourseListToTable(null);
            }
        });
    }
    // 删除课程按钮被点击
    private void buttonDeleteCourseMouseClicked(MouseEvent e) {
        deleteCourse();
        showCourseListToTable(null);
    }
    // 删除课程
    private void  deleteCourse(){
        // 获取选中的行
        int[] rows = tableCourseList.getSelectedRows();
        if (rows.length == 0){
            JOptionPane.showMessageDialog(null, "请选择要删除的课程");
            return;
        }
        // 创建一个课程列表
        List<Course> courses = new ArrayList<>();
        // 遍历选中的行
        for (int row : rows) {
            // 创建一个课程对象
            Course course = new Course();
            // 对这个课程对象进行赋值
            course.setCourseId((Integer) tableCourseList.getValueAt(row, 0));
            // 将这个课程对象添加到课程列表中
            courses.add(course);
        }
        // 删除课程
        boolean result = new AdministratorServiceImpl().deleteByCourseList(courses);
        // 判断是否删除成功
        if (result) {
            JOptionPane.showMessageDialog(null, "删除成功");
        } else {
            JOptionPane.showMessageDialog(null, "删除失败");
        }
    }
    // 查询课程按钮被点击
    private void buttonQueryCourseMouseClicked(MouseEvent e) {
        try {
            List<Course> courses = queryCourse();
            // 刷新一下
            showCourseListToTable(courses);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // 查询课程
    private List<Course> queryCourse() throws Exception {
        // 获取课程id
        String courseId = inputCourseId.getText();
        // 获取课程名
        String courseName = inputCourseName.getText();
        // 获取学院
        String collegeName = Objects.equals(Objects.requireNonNull(selectCourseCollege.getSelectedItem())
                .toString(), "不限") ? null : selectCourseCollege.getSelectedItem().toString();
        // 创建一个课程对象
        Course course = new Course();
        // 对这个课程对象进行赋值
        if (!courseId.isEmpty()) {
            course.setCourseId(Integer.parseInt(courseId));
        }
        course.setCourseName(courseName);
        if (collegeName != null) {
            course.setCollegeId(new CollegeServiceImpl().getCollegeIdByName(collegeName));
        }
        // 获取结果并返回
        return new AdministratorServiceImpl().getCourseByCourse(course);
    }
    // 导出课程按钮被点击
    private void buttonExportCourseMouseClicked(MouseEvent e) {
        exportCourse();
    }
    // 导出课程
    private void exportCourse(){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("课程列表");
        Row row = sheet.createRow(0);
        for (int i = 0; i < tableCourseList.getColumnCount(); i++) {
            row.createCell(i).setCellValue(tableCourseList.getColumnName(i));
        }
        for (int i = 0; i < tableCourseList.getRowCount(); i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < tableCourseList.getColumnCount(); j++) {
                String value = "";
                if (tableCourseList.getValueAt(i, j) != null) {
                    value = tableCourseList.getValueAt(i, j).toString();
                }
                row.createCell(j).setCellValue(value);
            }
        }
        // 导出
        // 创建一个文件选择器
        JFileChooser fileChooser = new JFileChooser();
        // 设置文件选择器的标题
        fileChooser.setDialogTitle("请选择导出路径");
        // 设置文件选择器的默认路径
        fileChooser.setCurrentDirectory(new java.io.File("."));
        // 设置文件选择器的选择模式
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        // 打开文件选择器
        int result = fileChooser.showSaveDialog(null);
        // 判断是否点击了保存按钮
        if (result == JFileChooser.APPROVE_OPTION) {
            // 获取选择的文件夹
            try (FileOutputStream fos = new FileOutputStream(fileChooser.getSelectedFile() + ".xlsx")) {
                workbook.write(fos);
                JOptionPane.showMessageDialog(null, "导出成功");
                workbook.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "导出失败");
                throw new RuntimeException(e);
            }
        }

    }
    // 展示各学院信息到表格上
    private void showCollegeInfoToTable() {
        // 获取学院列表
        List<College> colleges = null;
        try {
            colleges = new CollegeServiceImpl().getAllCollege();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 创建表格模型
        DefaultTableModel model = (DefaultTableModel) tableCollegeInfo.getModel();
        // 清空表格
        model.setRowCount(0);
        // 遍历学院列表
        for (College college : colleges) {
            // 添加一行
            model.addRow(new Object[]{
                    college.getCollegeId(),
                    college.getCollegeName(),
                    new AdministratorServiceImpl().getTeacherCountByCollegeId(college.getCollegeId()),
                    new AdministratorServiceImpl().getStudentCountByCollegeId(college.getCollegeId()),
            });
        }
        // 设置表格内容居中
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(JLabel.CENTER);
        tableCollegeInfo.setDefaultRenderer(Object.class, r);
        tableCollegeInfo.setDefaultRenderer(Integer.class, r);


    }
    // 修改课程按钮被点击
    private void buttonUpdateCourseMouseClicked(MouseEvent e) {
        // 获取当前选中的行
        int row = tableCourseList.getSelectedRow();
        // 判断是否选中
        if (row == -1) {
            JOptionPane.showMessageDialog(null, "请选择要修改的课程");
            return;
        }
        // 获取课程id
        Integer courseId = (Integer) tableCourseList.getValueAt(row, 0);
        // 获取课程对象
        Course course = null;
        try {
            course = new AdministratorServiceImpl().getCourseById(courseId);
            AddAndUpdateCourse addAndUpdateCourse = new AddAndUpdateCourse(this, course);
            addAndUpdateCourse.setVisible(true);
            // 刷新一下
            addAndUpdateCourse.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    showCourseListToTable(null);
                }
            });
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    // 删除学院按钮被点击
    private void buttonDeleteCollegeMouseClicked(MouseEvent e) {
        deleteCollege();
        // 刷新一下
        showCollegeInfoToTable();
    }
    // 删除学院
    private void deleteCollege(){
        // 获取选中的行
        int[] rows = tableCollegeInfo.getSelectedRows();
        if (rows.length == 0){
            JOptionPane.showMessageDialog(null, "请选择要删除的学院");
            return;
        }
        // 创建一个学院列表
        List<College> colleges = new ArrayList<>();
        // 遍历选中的行
        for (int row : rows) {
            // 创建一个学院对象
            College college = new College();
            // 对这个学院对象进行赋值
            college.setCollegeId((Integer) tableCollegeInfo.getValueAt(row, 0));
            // 将这个学院对象添加到学院列表中
            colleges.add(college);
        }
        // 删除学院
        boolean result = new AdministratorServiceImpl().deleteCollegeByList(colleges);
        // 判断是否删除成功
        if (result) {
            JOptionPane.showMessageDialog(null, "删除成功");
        } else {
            JOptionPane.showMessageDialog(null, "删除失败");
        }
    }
    // 添加学院按钮被点击
    private void button1MouseClicked(MouseEvent e) {
        // 创建一个窗口
        AddCollege addCollege = new AddCollege(this);
        // 设置为可见
        addCollege.setVisible(true);
        // 刷新一下
        addCollege.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                showCollegeInfoToTable();
            }
        });
    }
    private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
    tabbedPaneMenu = new JTabbedPane();
    panelMain = new JPanel();
    panelForStudentSexCount = new JPanel();
    label1 = new JLabel();
    labelStudentCount = new JLabel();
    panelForTeacherAgeCount = new JPanel();
    label2 = new JLabel();
    labelTeacherCount = new JLabel();
    label13 = new JLabel();
    panelStudentMange = new JPanel();
    scrollPane1 = new JScrollPane();
    tableStudentList = new JTable();
    buttonExportStudent = new JButton();
    buttonAddStudent = new JButton();
    buttonUpdateStudent = new JButton();
    buttonDeleteStudent = new JButton();
    buttonQuery = new JButton();
    panelCountStudentByCollege = new JPanel();
    label3 = new JLabel();
    inputStudentId = new JTextField();
    label4 = new JLabel();
    inputStudentName = new JTextField();
    label5 = new JLabel();
    selectBoxStudentSex = new JComboBox<>();
    label6 = new JLabel();
    selectBoxStudentCollege = new JComboBox<>();
    label7 = new JLabel();
    labelStudentListCount = new JLabel();
    panelTeacherMain = new JPanel();
    scrollPane2 = new JScrollPane();
    tableTeacherList = new JTable();
    buttonExportForTeacher = new JButton();
    buttonAddTeacher = new JButton();
    buttonUpdateTeacher = new JButton();
    buttonDeleteStudent2 = new JButton();
    buttonQueryForTeacher = new JButton();
    panelCountTeacherByPositon = new JPanel();
    label8 = new JLabel();
    inputTeacherId = new JTextField();
    label9 = new JLabel();
    inputTeacherName = new JTextField();
    label10 = new JLabel();
    selectBoxTeacherSex = new JComboBox<>();
    label11 = new JLabel();
    selectBoxTeacherCollege = new JComboBox<>();
    label12 = new JLabel();
    labelForTeacherCount = new JLabel();
    panelCollegeMain = new JPanel();
    scrollPane3 = new JScrollPane();
    tableCollegeInfo = new JTable();
    button1 = new JButton();
    buttonDeleteCollege = new JButton();
    label19 = new JLabel();
    label20 = new JLabel();
    panelCourseMange = new JPanel();
    scrollPaneCourseMange = new JScrollPane();
    tableCourseList = new JTable();
    buttonAddCourse = new JButton();
    buttonDeleteCourse = new JButton();
    buttonUpdateCourse = new JButton();
    buttonQueryCourse = new JButton();
    buttonExportCourse = new JButton();
    label16 = new JLabel();
    inputCourseId = new JTextField();
    label17 = new JLabel();
    inputCourseName = new JTextField();
    label18 = new JLabel();
    selectCourseCollege = new JComboBox<>();
    panelNoticeMange = new JPanel();
    scrollPaneNotice = new JScrollPane();
    tableNoticeList = new JTable();
    buttonPublishNotice = new JButton();
    buttonDeleteNotice = new JButton();
    label14 = new JLabel();
    inputNoticeContain = new JTextField();
    label15 = new JLabel();
    inputNoticeId = new JTextField();
    buttonQueryNotice = new JButton();

    //======== this ========
    setTitle("\u884c\u77e5\u6559\u52a1\u7ba1\u7406\u7cfb\u7edf-\u7ba1\u7406\u5458\u4e3b\u9875\u9762   by \u8983\u60e0\u901a");
    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    setResizable(false);
    addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            thisWindowClosing(e);
        }
    });
    var contentPane = getContentPane();
    contentPane.setLayout(null);

    //======== tabbedPaneMenu ========
    {
        tabbedPaneMenu.setTabPlacement(SwingConstants.LEFT);
        tabbedPaneMenu.addChangeListener(e -> tabbedPaneMenuStateChanged(e));

        //======== panelMain ========
        {
            panelMain.setLayout(null);

            //======== panelForStudentSexCount ========
            {
                panelForStudentSexCount.setLayout(null);
            }
            panelMain.add(panelForStudentSexCount);
            panelForStudentSexCount.setBounds(0, 395, 420, 275);

            //---- label1 ----
            label1.setText("\u5728\u6821\u5b66\u751f\u4eba\u6570:");
            panelMain.add(label1);
            label1.setBounds(new Rectangle(new Point(5, 25), label1.getPreferredSize()));

            //---- labelStudentCount ----
            labelStudentCount.setText("text");
            panelMain.add(labelStudentCount);
            labelStudentCount.setBounds(105, 25, 70, labelStudentCount.getPreferredSize().height);

            //======== panelForTeacherAgeCount ========
            {
                panelForTeacherAgeCount.setLayout(null);
            }
            panelMain.add(panelForTeacherAgeCount);
            panelForTeacherAgeCount.setBounds(425, 395, 440, 275);

            //---- label2 ----
            label2.setText("\u5728\u6821\u6559\u804c\u5de5\u4eba\u6570:");
            panelMain.add(label2);
            label2.setBounds(5, 55, 105, 17);

            //---- labelTeacherCount ----
            labelTeacherCount.setText("text");
            panelMain.add(labelTeacherCount);
            labelTeacherCount.setBounds(105, 55, 50, labelTeacherCount.getPreferredSize().height);

            //---- label13 ----
            label13.setText("\u5f85     \u53d1    \u6398   \u533a");
            label13.setFont(new Font("\u5b8b\u4f53", Font.PLAIN, 36));
            label13.setForeground(Color.red);
            panelMain.add(label13);
            label13.setBounds(220, 100, 415, 250);
        }
        tabbedPaneMenu.addTab("\u603b\u89c8", panelMain);

        //======== panelStudentMange ========
        {
            panelStudentMange.setLayout(null);

            //======== scrollPane1 ========
            {

                //---- tableStudentList ----
                tableStudentList.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u5b66\u53f7", "\u5b66\u9662", "\u73ed\u7ea7", "\u59d3\u540d", "\u5e74\u9f84", "\u6027\u522b", "\u8054\u7cfb\u7535\u8bdd", "\u767b\u5f55\u5bc6\u7801", "\u5bb6\u5ead\u4f4f\u5740"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, String.class, Integer.class, String.class, Integer.class, String.class, String.class, String.class, String.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false, false, false, false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                scrollPane1.setViewportView(tableStudentList);
            }
            panelStudentMange.add(scrollPane1);
            scrollPane1.setBounds(5, 50, 855, scrollPane1.getPreferredSize().height);

            //---- buttonExportStudent ----
            buttonExportStudent.setText("\u5bfc\u51fa");
            buttonExportStudent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonExportStudentMouseClicked(e);
                }
            });
            panelStudentMange.add(buttonExportStudent);
            buttonExportStudent.setBounds(780, 5, 78, 30);

            //---- buttonAddStudent ----
            buttonAddStudent.setText("\u6dfb\u52a0");
            buttonAddStudent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonAddStudentMouseClicked(e);
                }
            });
            panelStudentMange.add(buttonAddStudent);
            buttonAddStudent.setBounds(5, 5, 78, 30);

            //---- buttonUpdateStudent ----
            buttonUpdateStudent.setText("\u4fee\u6539");
            buttonUpdateStudent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonUpdateStudentMouseClicked(e);
                }
            });
            panelStudentMange.add(buttonUpdateStudent);
            buttonUpdateStudent.setBounds(90, 5, 78, 30);

            //---- buttonDeleteStudent ----
            buttonDeleteStudent.setText("\u5220\u9664");
            buttonDeleteStudent.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonDeleteStudentMouseClicked(e);
                }
            });
            panelStudentMange.add(buttonDeleteStudent);
            buttonDeleteStudent.setBounds(185, 5, 78, 30);

            //---- buttonQuery ----
            buttonQuery.setText("\u67e5\u8be2");
            buttonQuery.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonQueryMouseClicked(e);
                }
            });
            panelStudentMange.add(buttonQuery);
            buttonQuery.setBounds(690, 5, 78, 30);

            //======== panelCountStudentByCollege ========
            {
                panelCountStudentByCollege.setLayout(null);
            }
            panelStudentMange.add(panelCountStudentByCollege);
            panelCountStudentByCollege.setBounds(0, 475, 345, 200);

            //---- label3 ----
            label3.setText("\u59d3\u540d");
            panelStudentMange.add(label3);
            label3.setBounds(new Rectangle(new Point(265, 15), label3.getPreferredSize()));
            panelStudentMange.add(inputStudentId);
            inputStudentId.setBounds(395, 10, 70, 25);

            //---- label4 ----
            label4.setText("\u5b66\u53f7");
            panelStudentMange.add(label4);
            label4.setBounds(new Rectangle(new Point(365, 15), label4.getPreferredSize()));
            panelStudentMange.add(inputStudentName);
            inputStudentName.setBounds(290, 10, 70, 25);

            //---- label5 ----
            label5.setText("\u6027\u522b");
            panelStudentMange.add(label5);
            label5.setBounds(new Rectangle(new Point(470, 15), label5.getPreferredSize()));

            //---- selectBoxStudentSex ----
            selectBoxStudentSex.setModel(new DefaultComboBoxModel<>(new String[] {
                "\u4e0d\u9650",
                "\u7537",
                "\u5973"
            }));
            panelStudentMange.add(selectBoxStudentSex);
            selectBoxStudentSex.setBounds(500, 10, 60, selectBoxStudentSex.getPreferredSize().height);

            //---- label6 ----
            label6.setText("\u5b66\u9662");
            panelStudentMange.add(label6);
            label6.setBounds(new Rectangle(new Point(565, 15), label6.getPreferredSize()));

            //---- selectBoxStudentCollege ----
            selectBoxStudentCollege.setModel(new DefaultComboBoxModel<>(new String[] {
                "\u4e0d\u9650"
            }));
            panelStudentMange.add(selectBoxStudentCollege);
            selectBoxStudentCollege.setBounds(595, 10, 90, selectBoxStudentCollege.getPreferredSize().height);

            //---- label7 ----
            label7.setText("\u5217\u8868\u4eba\u6570");
            panelStudentMange.add(label7);
            label7.setBounds(new Rectangle(new Point(360, 495), label7.getPreferredSize()));

            //---- labelStudentListCount ----
            labelStudentListCount.setText("text");
            panelStudentMange.add(labelStudentListCount);
            labelStudentListCount.setBounds(415, 495, 70, labelStudentListCount.getPreferredSize().height);
        }
        tabbedPaneMenu.addTab("\u5b66\u751f\u7ba1\u7406", panelStudentMange);

        //======== panelTeacherMain ========
        {
            panelTeacherMain.setLayout(null);

            //======== scrollPane2 ========
            {

                //---- tableTeacherList ----
                tableTeacherList.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u5de5\u53f7", "\u5b66\u9662", "\u7ba1\u7406\u73ed\u7ea7", "\u59d3\u540d", "\u5e74\u9f84", "\u6027\u522b", "\u804c\u4f4d", "\u8054\u7cfb\u7535\u8bdd", "\u767b\u5f55\u5bc6\u7801"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, String.class, Integer.class, String.class, Integer.class, String.class, String.class, String.class, String.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false, false, false, false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                scrollPane2.setViewportView(tableTeacherList);
            }
            panelTeacherMain.add(scrollPane2);
            scrollPane2.setBounds(5, 50, 855, scrollPane2.getPreferredSize().height);

            //---- buttonExportForTeacher ----
            buttonExportForTeacher.setText("\u5bfc\u51fa");
            buttonExportForTeacher.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonExportForTeacherMouseClicked(e);
                }
            });
            panelTeacherMain.add(buttonExportForTeacher);
            buttonExportForTeacher.setBounds(780, 5, 78, 30);

            //---- buttonAddTeacher ----
            buttonAddTeacher.setText("\u6dfb\u52a0");
            buttonAddTeacher.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonAddTeacherMouseClicked(e);
                }
            });
            panelTeacherMain.add(buttonAddTeacher);
            buttonAddTeacher.setBounds(5, 5, 78, 30);

            //---- buttonUpdateTeacher ----
            buttonUpdateTeacher.setText("\u4fee\u6539");
            buttonUpdateTeacher.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonUpdateTeacherMouseClicked(e);
                }
            });
            panelTeacherMain.add(buttonUpdateTeacher);
            buttonUpdateTeacher.setBounds(90, 5, 78, 30);

            //---- buttonDeleteStudent2 ----
            buttonDeleteStudent2.setText("\u5220\u9664");
            buttonDeleteStudent2.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonDeleteStudent2MouseClicked(e);
                }
            });
            panelTeacherMain.add(buttonDeleteStudent2);
            buttonDeleteStudent2.setBounds(185, 5, 78, 30);

            //---- buttonQueryForTeacher ----
            buttonQueryForTeacher.setText("\u67e5\u8be2");
            buttonQueryForTeacher.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonQueryForTeacherMouseClicked(e);
                }
            });
            panelTeacherMain.add(buttonQueryForTeacher);
            buttonQueryForTeacher.setBounds(690, 5, 78, 30);

            //======== panelCountTeacherByPositon ========
            {
                panelCountTeacherByPositon.setLayout(null);
            }
            panelTeacherMain.add(panelCountTeacherByPositon);
            panelCountTeacherByPositon.setBounds(0, 475, 345, 200);

            //---- label8 ----
            label8.setText("\u59d3\u540d");
            panelTeacherMain.add(label8);
            label8.setBounds(new Rectangle(new Point(265, 15), label8.getPreferredSize()));
            panelTeacherMain.add(inputTeacherId);
            inputTeacherId.setBounds(395, 10, 70, 25);

            //---- label9 ----
            label9.setText("\u5de5\u53f7");
            panelTeacherMain.add(label9);
            label9.setBounds(new Rectangle(new Point(365, 15), label9.getPreferredSize()));
            panelTeacherMain.add(inputTeacherName);
            inputTeacherName.setBounds(290, 10, 70, 25);

            //---- label10 ----
            label10.setText("\u6027\u522b");
            panelTeacherMain.add(label10);
            label10.setBounds(new Rectangle(new Point(470, 15), label10.getPreferredSize()));

            //---- selectBoxTeacherSex ----
            selectBoxTeacherSex.setModel(new DefaultComboBoxModel<>(new String[] {
                "\u4e0d\u9650",
                "\u7537",
                "\u5973"
            }));
            panelTeacherMain.add(selectBoxTeacherSex);
            selectBoxTeacherSex.setBounds(500, 10, 60, selectBoxTeacherSex.getPreferredSize().height);

            //---- label11 ----
            label11.setText("\u5b66\u9662");
            panelTeacherMain.add(label11);
            label11.setBounds(new Rectangle(new Point(565, 15), label11.getPreferredSize()));

            //---- selectBoxTeacherCollege ----
            selectBoxTeacherCollege.setModel(new DefaultComboBoxModel<>(new String[] {
                "\u4e0d\u9650"
            }));
            panelTeacherMain.add(selectBoxTeacherCollege);
            selectBoxTeacherCollege.setBounds(595, 10, 90, selectBoxTeacherCollege.getPreferredSize().height);

            //---- label12 ----
            label12.setText("\u5217\u8868\u4eba\u6570");
            panelTeacherMain.add(label12);
            label12.setBounds(new Rectangle(new Point(360, 495), label12.getPreferredSize()));

            //---- labelForTeacherCount ----
            labelForTeacherCount.setText("text");
            panelTeacherMain.add(labelForTeacherCount);
            labelForTeacherCount.setBounds(415, 495, 70, labelForTeacherCount.getPreferredSize().height);
        }
        tabbedPaneMenu.addTab("\u6559\u804c\u5de5\u7ba1\u7406", panelTeacherMain);

        //======== panelCollegeMain ========
        {
            panelCollegeMain.setLayout(null);

            //======== scrollPane3 ========
            {

                //---- tableCollegeInfo ----
                tableCollegeInfo.setShowHorizontalLines(false);
                tableCollegeInfo.setShowVerticalLines(false);
                tableCollegeInfo.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u5b66\u9662id", "\u5b66\u9662\u540d\u79f0", "\u6559\u804c\u5de5\u4eba\u6570", "\u5b66\u751f\u4eba\u6570"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, String.class, Integer.class, Integer.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                scrollPane3.setViewportView(tableCollegeInfo);
            }
            panelCollegeMain.add(scrollPane3);
            scrollPane3.setBounds(5, 80, 855, scrollPane3.getPreferredSize().height);

            //---- button1 ----
            button1.setText("\u589e\u52a0\u5b66\u9662");
            button1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    button1MouseClicked(e);
                }
            });
            panelCollegeMain.add(button1);
            button1.setBounds(15, 20, 85, 30);

            //---- buttonDeleteCollege ----
            buttonDeleteCollege.setText("\u5220\u9664\u5b66\u9662");
            buttonDeleteCollege.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonDeleteCollegeMouseClicked(e);
                }
            });
            panelCollegeMain.add(buttonDeleteCollege);
            buttonDeleteCollege.setBounds(125, 20, 85, 30);

            //---- label19 ----
            label19.setText("\u6211    \u7d2f    \u4e86\uff0c\u6b64      \u533a       \u57df     \u5f85   \u53d1       \u6398    \u3002\u3002\u3002");
            panelCollegeMain.add(label19);
            label19.setBounds(135, 570, 605, 75);

            //---- label20 ----
            label20.setText("\u5f85           \u6316                           \u6398               \u533a");
            panelCollegeMain.add(label20);
            label20.setBounds(265, 25, 575, label20.getPreferredSize().height);
        }
        tabbedPaneMenu.addTab("\u5b66\u9662\u7ba1\u7406", panelCollegeMain);

        //======== panelCourseMange ========
        {
            panelCourseMange.setLayout(null);

            //======== scrollPaneCourseMange ========
            {

                //---- tableCourseList ----
                tableCourseList.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u8bfe\u7a0b\u7f16\u53f7", "\u8bfe\u7a0b\u540d\u79f0", "\u5f00\u8bbe\u5b66\u9662"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, String.class, String.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                tableCourseList.setShowHorizontalLines(false);
                tableCourseList.setShowVerticalLines(false);
                scrollPaneCourseMange.setViewportView(tableCourseList);
            }
            panelCourseMange.add(scrollPaneCourseMange);
            scrollPaneCourseMange.setBounds(0, 80, 860, 585);

            //---- buttonAddCourse ----
            buttonAddCourse.setText("\u6dfb\u52a0\u8bfe\u7a0b");
            buttonAddCourse.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonAddCourseMouseClicked(e);
                }
            });
            panelCourseMange.add(buttonAddCourse);
            buttonAddCourse.setBounds(new Rectangle(new Point(5, 35), buttonAddCourse.getPreferredSize()));

            //---- buttonDeleteCourse ----
            buttonDeleteCourse.setText("\u5220\u9664\u8bfe\u7a0b");
            buttonDeleteCourse.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonDeleteCourseMouseClicked(e);
                }
            });
            panelCourseMange.add(buttonDeleteCourse);
            buttonDeleteCourse.setBounds(new Rectangle(new Point(100, 35), buttonDeleteCourse.getPreferredSize()));

            //---- buttonUpdateCourse ----
            buttonUpdateCourse.setText("\u4fee\u6539\u8bfe\u7a0b");
            buttonUpdateCourse.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonUpdateCourseMouseClicked(e);
                }
            });
            panelCourseMange.add(buttonUpdateCourse);
            buttonUpdateCourse.setBounds(new Rectangle(new Point(190, 35), buttonUpdateCourse.getPreferredSize()));

            //---- buttonQueryCourse ----
            buttonQueryCourse.setText("\u67e5\u8be2\u8bfe\u7a0b");
            buttonQueryCourse.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonQueryCourseMouseClicked(e);
                }
            });
            panelCourseMange.add(buttonQueryCourse);
            buttonQueryCourse.setBounds(new Rectangle(new Point(680, 35), buttonQueryCourse.getPreferredSize()));

            //---- buttonExportCourse ----
            buttonExportCourse.setText("\u5bfc\u51fa\u5217\u8868");
            buttonExportCourse.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonExportCourseMouseClicked(e);
                }
            });
            panelCourseMange.add(buttonExportCourse);
            buttonExportCourse.setBounds(new Rectangle(new Point(775, 35), buttonExportCourse.getPreferredSize()));

            //---- label16 ----
            label16.setText("\u8bfe\u7a0bid");
            panelCourseMange.add(label16);
            label16.setBounds(new Rectangle(new Point(280, 40), label16.getPreferredSize()));
            panelCourseMange.add(inputCourseId);
            inputCourseId.setBounds(320, 35, 75, inputCourseId.getPreferredSize().height);

            //---- label17 ----
            label17.setText("\u8bfe\u7a0b\u540d\u79f0");
            panelCourseMange.add(label17);
            label17.setBounds(new Rectangle(new Point(400, 40), label17.getPreferredSize()));
            panelCourseMange.add(inputCourseName);
            inputCourseName.setBounds(455, 35, 65, inputCourseName.getPreferredSize().height);

            //---- label18 ----
            label18.setText("\u8d1f\u8d23\u5b66\u9662");
            panelCourseMange.add(label18);
            label18.setBounds(new Rectangle(new Point(525, 40), label18.getPreferredSize()));

            //---- selectCourseCollege ----
            selectCourseCollege.setModel(new DefaultComboBoxModel<>(new String[] {
                "\u4e0d\u9650"
            }));
            panelCourseMange.add(selectCourseCollege);
            selectCourseCollege.setBounds(585, 35, 90, selectCourseCollege.getPreferredSize().height);
        }
        tabbedPaneMenu.addTab("\u8bfe\u7a0b\u7ba1\u7406", panelCourseMange);

        //======== panelNoticeMange ========
        {
            panelNoticeMange.setLayout(null);

            //======== scrollPaneNotice ========
            {

                //---- tableNoticeList ----
                tableNoticeList.setModel(new DefaultTableModel(
                    new Object[][] {
                    },
                    new String[] {
                        "\u516c\u544aid", "\u53d1\u6587\u673a\u5173", "\u4e3b\u9001\u673a\u5173", "\u53d1\u6587\u65f6\u95f4", "\u516c\u544a\u6807\u9898", "\u516c\u544a\u5185\u5bb9"
                    }
                ) {
                    Class<?>[] columnTypes = new Class<?>[] {
                        String.class, String.class, String.class, String.class, String.class, String.class
                    };
                    boolean[] columnEditable = new boolean[] {
                        false, false, false, false, false, false
                    };
                    @Override
                    public Class<?> getColumnClass(int columnIndex) {
                        return columnTypes[columnIndex];
                    }
                    @Override
                    public boolean isCellEditable(int rowIndex, int columnIndex) {
                        return columnEditable[columnIndex];
                    }
                });
                tableNoticeList.setShowVerticalLines(false);
                tableNoticeList.setShowHorizontalLines(false);
                scrollPaneNotice.setViewportView(tableNoticeList);
            }
            panelNoticeMange.add(scrollPaneNotice);
            scrollPaneNotice.setBounds(0, 60, 865, 610);

            //---- buttonPublishNotice ----
            buttonPublishNotice.setText("\u53d1\u5e03\u516c\u544a");
            buttonPublishNotice.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonPublishNoticeMouseClicked(e);
                }
            });
            panelNoticeMange.add(buttonPublishNotice);
            buttonPublishNotice.setBounds(15, 15, 125, 30);

            //---- buttonDeleteNotice ----
            buttonDeleteNotice.setText("\u5220\u9664\u516c\u544a");
            buttonDeleteNotice.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonDeleteNoticeMouseClicked(e);
                }
            });
            panelNoticeMange.add(buttonDeleteNotice);
            buttonDeleteNotice.setBounds(155, 15, 125, 30);

            //---- label14 ----
            label14.setText("\u516c\u544a\u6807\u9898\u6216\u5185\u5bb9\u5305\u542b:");
            panelNoticeMange.add(label14);
            label14.setBounds(new Rectangle(new Point(295, 20), label14.getPreferredSize()));
            panelNoticeMange.add(inputNoticeContain);
            inputNoticeContain.setBounds(430, 15, 140, inputNoticeContain.getPreferredSize().height);

            //---- label15 ----
            label15.setText("\u516c\u544aid");
            panelNoticeMange.add(label15);
            label15.setBounds(new Rectangle(new Point(590, 20), label15.getPreferredSize()));
            panelNoticeMange.add(inputNoticeId);
            inputNoticeId.setBounds(630, 15, 95, inputNoticeId.getPreferredSize().height);

            //---- buttonQueryNotice ----
            buttonQueryNotice.setText("\u67e5\u8be2\u516c\u544a");
            buttonQueryNotice.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    buttonQueryNoticeMouseClicked(e);
                }
            });
            panelNoticeMange.add(buttonQueryNotice);
            buttonQueryNotice.setBounds(new Rectangle(new Point(740, 15), buttonQueryNotice.getPreferredSize()));
        }
        tabbedPaneMenu.addTab("\u516c\u544a\u7ba1\u7406", panelNoticeMange);
    }
    contentPane.add(tabbedPaneMenu);
    tabbedPaneMenu.setBounds(5, 10, 955, 670);

    contentPane.setPreferredSize(new Dimension(960, 710));
    pack();
    setLocationRelativeTo(getOwner());
    // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
}
    // 展示学生列表到表格上
    private void showStudentInfoToTable() throws Exception {
        DefaultTableModel model = (DefaultTableModel) tableStudentList.getModel();
        model.setRowCount(0);
        for (Student student : students) {
            model.addRow(new Object[]{
                    student.getStudentId(),
                    new CollegeServiceImpl().getCollegeNameById(student.getCollegeId()),
                    student.getClassId(),
                    student.getName(),
                    student.getAge(),
                    student.getSex() == 1 ? "男" : "女",
                    student.getPhoneNumber(),
                    student.getPassWord(),
                    student.getAddress()
            });
            // 设置表格内容居中
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(JLabel.CENTER);
            tableStudentList.setDefaultRenderer(Object.class, r);
            tableStudentList.setDefaultRenderer(Integer.class, r);
        }
        // 展示总人数到那啥标签上
        labelStudentListCount.setText(model.getRowCount() + "");
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JTabbedPane tabbedPaneMenu;
    private JPanel panelMain;
    private JPanel panelForStudentSexCount;
    private JLabel label1;
    private JLabel labelStudentCount;
    private JPanel panelForTeacherAgeCount;
    private JLabel label2;
    private JLabel labelTeacherCount;
    private JLabel label13;
    private JPanel panelStudentMange;
    private JScrollPane scrollPane1;
    private JTable tableStudentList;
    private JButton buttonExportStudent;
    private JButton buttonAddStudent;
    private JButton buttonUpdateStudent;
    private JButton buttonDeleteStudent;
    private JButton buttonQuery;
    private JPanel panelCountStudentByCollege;
    private JLabel label3;
    private JTextField inputStudentId;
    private JLabel label4;
    private JTextField inputStudentName;
    private JLabel label5;
    private JComboBox<String> selectBoxStudentSex;
    private JLabel label6;
    private JComboBox<String> selectBoxStudentCollege;
    private JLabel label7;
    private JLabel labelStudentListCount;
    private JPanel panelTeacherMain;
    private JScrollPane scrollPane2;
    private JTable tableTeacherList;
    private JButton buttonExportForTeacher;
    private JButton buttonAddTeacher;
    private JButton buttonUpdateTeacher;
    private JButton buttonDeleteStudent2;
    private JButton buttonQueryForTeacher;
    private JPanel panelCountTeacherByPositon;
    private JLabel label8;
    private JTextField inputTeacherId;
    private JLabel label9;
    private JTextField inputTeacherName;
    private JLabel label10;
    private JComboBox<String> selectBoxTeacherSex;
    private JLabel label11;
    private JComboBox<String> selectBoxTeacherCollege;
    private JLabel label12;
    private JLabel labelForTeacherCount;
    private JPanel panelCollegeMain;
    private JScrollPane scrollPane3;
    private JTable tableCollegeInfo;
    private JButton button1;
    private JButton buttonDeleteCollege;
    private JLabel label19;
    private JLabel label20;
    private JPanel panelCourseMange;
    private JScrollPane scrollPaneCourseMange;
    private JTable tableCourseList;
    private JButton buttonAddCourse;
    private JButton buttonDeleteCourse;
    private JButton buttonUpdateCourse;
    private JButton buttonQueryCourse;
    private JButton buttonExportCourse;
    private JLabel label16;
    private JTextField inputCourseId;
    private JLabel label17;
    private JTextField inputCourseName;
    private JLabel label18;
    private JComboBox<String> selectCourseCollege;
    private JPanel panelNoticeMange;
    private JScrollPane scrollPaneNotice;
    private JTable tableNoticeList;
    private JButton buttonPublishNotice;
    private JButton buttonDeleteNotice;
    private JLabel label14;
    private JTextField inputNoticeContain;
    private JLabel label15;
    private JTextField inputNoticeId;
    private JButton buttonQueryNotice;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on


    public static void main(String[] args) {
        // 使用FlatLaf皮肤包
        FlatLightLaf.install();
        try {
            UIManager.setLookAndFeel( new FlatLightLaf());
        } catch (Exception e) {
            System.out.println("皮肤包导入失败！");
        }
        new AdministratorControllerGui().setVisible(true);

    }
    }

