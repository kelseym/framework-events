package org.nrg.framework.node;

/**
 * The Class XnatNode.
 */
public class XnatNode {
	
	public static final String NODE_ID_NOT_CONFIGURED = "UNCONFIGURED";
	
	/** The node id. */
	private String nodeId;

	/**
	 * Gets the node id.
	 *
	 * @return the node id
	 */
	public String getNodeId() {
		return nodeId;
	}

	/**
	 * Sets the node id.
	 *
	 * @param nodeId the new node id
	 */
	public void setNodeId(final String nodeId) {
		this.nodeId = nodeId;
	}

}
