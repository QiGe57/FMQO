package Common;

public class SeverInfo {
	private String sesameServer;
	private String repositoryID;

	public SeverInfo(String sesameServer, String repositoryID) {
		super();
		this.sesameServer = sesameServer;
		this.repositoryID = repositoryID;
	}

	public SeverInfo() {
	}

	public String getSesameServer() {
		return sesameServer;
	}

	public void setSesameServer(String sesameServer) {
		this.sesameServer = sesameServer;
	}

	public String getRepositoryID() {
		return repositoryID;
	}

	public void setRepositoryID(String repositoryID) {
		this.repositoryID = repositoryID;
	}

	@Override
	public String toString() {
		return "SeverInfo [sesameServer=" + sesameServer + ", repositoryID="
				+ repositoryID + "]";
	}

}
