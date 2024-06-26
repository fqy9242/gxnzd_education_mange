/*
 * Created by JFormDesigner on Sat May 25 23:08:15 CST 2024
 */

package cn.qht2005.www.view.gui;

import java.awt.event.*;
import cn.qht2005.www.pojo.Leave;
import cn.qht2005.www.pojo.people.Student;
import cn.qht2005.www.service.impl.StudentServiceImpl;
import cn.qht2005.www.service.impl.TeacherServiceImpl;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import javax.swing.*;

/**
 * @author 覃
 */
public class DisposeLeaveWindowsAndController extends JDialog {
    // 请假对象
    private final Leave leave;
    public DisposeLeaveWindowsAndController(Window owner, Leave leave) {
        super(owner);
        this.leave = leave;
        initComponents();
        try {
            init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
    // 初始化
    private void init() throws Exception {
        // 格式化时间
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        // 获取学生对象
        Student student = new TeacherServiceImpl().getStudentById(leave.getUserId());
        labelForName.setText(student.getName());
        labelForUserId.setText(student.getStudentId());
        labelForLeaveType.setText(leave.getLeaveType() == 1? "事假" : leave.getLeaveType() == 2? "病假" : "其他");
        labelForLeaveStartTime.setText(leave.getLeaveStartTime().format(dateTimeFormatter));
        labelForLeaveEndTime.setText(leave.getLeaveEndTime().format(dateTimeFormatter));
        textAreaForApplyReason.setText(leave.getLeaveReason());
    }
    // 请假处理
    public void disposeLeave() {
        // 获取回复
        String response = textAreaResponse.getText();
        // 获取处理状态 0 拒绝 1 同意
        short applicationStatus = -1;
        if (radioButtonAgree.isSelected()) {
            applicationStatus = 1;
        } else if (radioButtonResulse.isSelected()) {
            applicationStatus = 0;
        }
        // 更新请假状态
        leave.setApplicationStatus(applicationStatus);
        leave.setResponse(response);
        try {
            new TeacherServiceImpl().disposeApply(leave);
            JOptionPane.showMessageDialog(this, "处理成功");
            this.setVisible(false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 提交按钮点击事件
    private void buttonCommitMouseClicked(MouseEvent e) {
        disposeLeave();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        scrollPane1 = new JScrollPane();
        textAreaResponse = new JTextArea();
        buttonCommit = new JButton();
        radioButtonAgree = new JRadioButton();
        radioButtonResulse = new JRadioButton();
        label1 = new JLabel();
        label2 = new JLabel();
        label3 = new JLabel();
        labelForName = new JLabel();
        labelForUserId = new JLabel();
        label6 = new JLabel();
        labelForLeaveType = new JLabel();
        label8 = new JLabel();
        labelForLeaveStartTime = new JLabel();
        label10 = new JLabel();
        labelForLeaveEndTime = new JLabel();
        scrollPane2 = new JScrollPane();
        textAreaForApplyReason = new JTextArea();
        label12 = new JLabel();

        //======== this ========
        setTitle("\u6279\u5047");
        setAlwaysOnTop(true);
        var contentPane = getContentPane();
        contentPane.setLayout(null);

        //======== scrollPane1 ========
        {

            //---- textAreaResponse ----
            textAreaResponse.setLineWrap(true);
            scrollPane1.setViewportView(textAreaResponse);
        }
        contentPane.add(scrollPane1);
        scrollPane1.setBounds(13, 50, 270, 135);

        //---- buttonCommit ----
        buttonCommit.setText("\u63d0\u4ea4");
        buttonCommit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonCommitMouseClicked(e);
            }
        });
        contentPane.add(buttonCommit);
        buttonCommit.setBounds(new Rectangle(new Point(185, 5), buttonCommit.getPreferredSize()));

        //---- radioButtonAgree ----
        radioButtonAgree.setText("\u540c\u610f");
        contentPane.add(radioButtonAgree);
        radioButtonAgree.setBounds(30, 9, 49, 21);

        //---- radioButtonResulse ----
        radioButtonResulse.setText("\u62d2\u7edd");
        contentPane.add(radioButtonResulse);
        radioButtonResulse.setBounds(105, 9, 49, 21);

        //---- label1 ----
        label1.setText("\u56de\u590d");
        contentPane.add(label1);
        label1.setBounds(new Rectangle(new Point(130, 35), label1.getPreferredSize()));

        //---- label2 ----
        label2.setText("\u59d3\u540d");
        contentPane.add(label2);
        label2.setBounds(new Rectangle(new Point(5, 210), label2.getPreferredSize()));

        //---- label3 ----
        label3.setText("\u5b66\u53f7");
        contentPane.add(label3);
        label3.setBounds(5, 230, 25, 17);

        //---- labelForName ----
        labelForName.setText("text");
        contentPane.add(labelForName);
        labelForName.setBounds(35, 210, 65, labelForName.getPreferredSize().height);

        //---- labelForUserId ----
        labelForUserId.setText("text");
        contentPane.add(labelForUserId);
        labelForUserId.setBounds(35, 230, 170, labelForUserId.getPreferredSize().height);

        //---- label6 ----
        label6.setText("\u8bf7\u5047\u7c7b\u578b");
        contentPane.add(label6);
        label6.setBounds(new Rectangle(new Point(160, 210), label6.getPreferredSize()));

        //---- labelForLeaveType ----
        labelForLeaveType.setText("text");
        contentPane.add(labelForLeaveType);
        labelForLeaveType.setBounds(220, 210, 60, labelForLeaveType.getPreferredSize().height);

        //---- label8 ----
        label8.setText("\u8bf7\u5047\u5f00\u59cb\u65f6\u95f4");
        contentPane.add(label8);
        label8.setBounds(new Rectangle(new Point(5, 255), label8.getPreferredSize()));

        //---- labelForLeaveStartTime ----
        labelForLeaveStartTime.setText("text");
        contentPane.add(labelForLeaveStartTime);
        labelForLeaveStartTime.setBounds(85, 255, 170, labelForLeaveStartTime.getPreferredSize().height);

        //---- label10 ----
        label10.setText("\u8bf7\u5047\u7ed3\u675f\u65f6\u95f4");
        contentPane.add(label10);
        label10.setBounds(new Rectangle(new Point(5, 280), label10.getPreferredSize()));

        //---- labelForLeaveEndTime ----
        labelForLeaveEndTime.setText("text");
        contentPane.add(labelForLeaveEndTime);
        labelForLeaveEndTime.setBounds(85, 280, 190, labelForLeaveEndTime.getPreferredSize().height);

        //======== scrollPane2 ========
        {

            //---- textAreaForApplyReason ----
            textAreaForApplyReason.setLineWrap(true);
            textAreaForApplyReason.setEditable(false);
            scrollPane2.setViewportView(textAreaForApplyReason);
        }
        contentPane.add(scrollPane2);
        scrollPane2.setBounds(13, 330, 270, 120);

        //---- label12 ----
        label12.setText("\u8bf7\u5047\u7406\u7531");
        contentPane.add(label12);
        label12.setBounds(new Rectangle(new Point(130, 310), label12.getPreferredSize()));

        contentPane.setPreferredSize(new Dimension(300, 485));
        pack();
        setLocationRelativeTo(getOwner());

        //---- buttonGroupDispose ----
        var buttonGroupDispose = new ButtonGroup();
        buttonGroupDispose.add(radioButtonAgree);
        buttonGroupDispose.add(radioButtonResulse);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JScrollPane scrollPane1;
    private JTextArea textAreaResponse;
    private JButton buttonCommit;
    private JRadioButton radioButtonAgree;
    private JRadioButton radioButtonResulse;
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;
    private JLabel labelForName;
    private JLabel labelForUserId;
    private JLabel label6;
    private JLabel labelForLeaveType;
    private JLabel label8;
    private JLabel labelForLeaveStartTime;
    private JLabel label10;
    private JLabel labelForLeaveEndTime;
    private JScrollPane scrollPane2;
    private JTextArea textAreaForApplyReason;
    private JLabel label12;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
