package hu.csekme.invoiceagent.beans;
import hu.csekme.invoiceagent.dao.XmlResponseDao;
import org.primefaces.model.DashboardColumn;
import org.primefaces.model.DashboardModel;
import org.primefaces.model.DefaultDashboardColumn;
import org.primefaces.model.DefaultDashboardModel;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
/**
 * Home Page bean
 * logs successful and unsuccessful receipt generations
 * while the application is running
 * @author Krisztián Csekme
 */
@Named
@ApplicationScoped
public class HomeBean implements Serializable {

  private int numberOfSuccessGeneratedReceipt;
  private int numberOfFailedGeneratedReceipt;
  private int serverRunning;
  private long startServer;
  private DashboardModel model;

  @Inject
  XmlResponseDao dao;

  @PostConstruct
  public void init() {
    startServer = System.currentTimeMillis();
    model = new DefaultDashboardModel();
    DashboardColumn column1 = new DefaultDashboardColumn();
    DashboardColumn column2 = new DefaultDashboardColumn();
    DashboardColumn column3 = new DefaultDashboardColumn();

    column1.addWidget("success");
    column1.addWidget("running");
    column2.addWidget("failed");
    column3.addWidget("current");

    model.addColumn(column1);
    model.addColumn(column2);
    model.addColumn(column3);
  }

  public void tick() {
    setServerRunning((int)(System.currentTimeMillis() - startServer)/1000);
  }

  public void increaseFailedReceipt() {
    setNumberOfFailedGeneratedReceipt(getNumberOfFailedGeneratedReceipt()+1);
  }

  public void increaseSuccessReceipt() {
    setNumberOfSuccessGeneratedReceipt(getNumberOfSuccessGeneratedReceipt()+1);
  }

  public int getNumberOfSuccessGeneratedReceipt() {
    return numberOfSuccessGeneratedReceipt;
  }

  public void setNumberOfSuccessGeneratedReceipt(int numberOfSuccessGeneratedReceipt) {
    this.numberOfSuccessGeneratedReceipt = numberOfSuccessGeneratedReceipt;
  }

  public int getNumberOfFailedGeneratedReceipt() {
    return numberOfFailedGeneratedReceipt;
  }

  public void setNumberOfFailedGeneratedReceipt(int numberOfFailedGeneratedReceipt) {
    this.numberOfFailedGeneratedReceipt = numberOfFailedGeneratedReceipt;
  }

  public DashboardModel getModel() {
    return model;
  }

  public void setModel(DashboardModel model) {
    this.model = model;
  }

  public XmlResponseDao getDao() {
    return dao;
  }

  public void setDao(XmlResponseDao dao) {
    this.dao = dao;
  }

  public int getServerRunning() {
    return serverRunning;
  }

  public void setServerRunning(int serverRunning) {
    this.serverRunning = serverRunning;
  }
}
