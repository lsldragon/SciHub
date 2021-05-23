package lsl.SciHub;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.alee.utils.jar.JarEntry;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.StreamProgress;
import cn.hutool.http.HttpUtil;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField doitextField;
	private JButton downloadButton;
	private JTextArea infoTextArea;

	private String sciHubURL = "https://sci-hub.mksa.top/";

	public MainFrame() {
		setTitle("Sci-Hub Desktop V1.0");

		setLocationRelativeTo(null);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(Color.GRAY, 1, true));
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		getContentPane().add(panel, gbc_panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		JLabel lblNewLabel = new JLabel("Enter DOI or DOI URL: ");
		lblNewLabel.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
		panel.add(lblNewLabel);

		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(Color.GRAY, 1, true));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 1;
		getContentPane().add(panel_1, gbc_panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));

		doitextField = new JTextField();
		doitextField.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
		panel_1.add(doitextField);
		doitextField.setColumns(10);

		downloadButton = new JButton("Fetch PDF");
		downloadButton.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
		panel_1.add(downloadButton);

		JScrollPane scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 2;
		getContentPane().add(scrollPane, gbc_scrollPane);

		infoTextArea = new JTextArea();
		infoTextArea.setLineWrap(true);
		infoTextArea.setEditable(false);
		infoTextArea.setText("Sci-Hub V1.0");
		infoTextArea.setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 16));
		scrollPane.setViewportView(infoTextArea);

		Image iconImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("logo.png"));
		setIconImage(iconImage);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setMainFrame();
		setResizable(false);

		initListeners();
	}

	private void initListeners() {
		downloadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				new Thread(new Runnable() {

					String fileName = "";

					@Override
					public void run() {

						try {

							infoTextArea.append("\ndownloading website source code...\n");

							String doiString = doitextField.getText().trim();
							String urlString = sciHubURL + doiString;

							if (doiString.startsWith("https")) {

								int begin = doiString.lastIndexOf("/");
								fileName = doiString.substring(begin + 1, doiString.length()) + ".pdf";
								System.out.println(fileName);
							} else {
								fileName = doiString.split("/")[1] + ".pdf";
							}

							String htmlString = HttpUtil.get(urlString);

							infoTextArea.append("analysing url...\n");
							Document document = Jsoup.parse(htmlString);
							Elements elements = document.select("#menu #buttons ul li a");
							String _downloadURLString = elements.attr("onclick");
							int first_symble = _downloadURLString.indexOf("=") + 2;
							int length = _downloadURLString.length() - 1;

							String downloadURLString = _downloadURLString.substring(first_symble, length);
							String downloadURL = downloadURLString.replaceAll("\\\\", "");

							HttpUtil.downloadFile(downloadURL, new File(fileName), new StreamProgress() {

								@Override
								public void start() {
									infoTextArea.append("donwloading .... \n");

									infoTextArea.setCaretPosition(infoTextArea.getText().length());
								}

								@Override
								public void progress(long progressSize) {
									infoTextArea.append("已下载" + ": " + FileUtil.readableFileSize(progressSize) + "\n");
									infoTextArea.setCaretPosition(infoTextArea.getText().length());
								}

								@Override
								public void finish() {
									infoTextArea.append("finished ... \n");
									infoTextArea.append("文件名: " + fileName);
									infoTextArea.setCaretPosition(infoTextArea.getText().length());
								}
							});

						} catch (Exception e2) {
							JOptionPane.showMessageDialog(null, e2.getMessage() + "\n Paper can't be downloaded !!!",
									"ERROR", JOptionPane.WARNING_MESSAGE);
						}
					}
				}).start();
			}
		});
	}

	private void setMainFrame() {

		Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
		int showWidth = (int) (resolution.getWidth() * 0.4);
		int showHeight = (int) (resolution.getHeight() * 0.4);
		getContentPane().setPreferredSize(new Dimension(showWidth, showHeight));
		pack();
		int x = (int) ((resolution.getWidth() - getWidth()) / 2);
		int y = (int) ((resolution.getHeight() - getHeight()) / 2);
		setLocation(x, y);
	}

}
