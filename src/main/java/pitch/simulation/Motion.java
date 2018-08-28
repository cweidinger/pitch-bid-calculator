package pitch.simulation;

import java.util.HashMap;
import java.util.Map;

public class Motion {

	public String type; // newDeal
	public int requestingIndex;
	public Map<Integer, String> indexRequests;
	public Map<Integer, String> indexResponses;

	public Motion(String type, int requestingIndex, Map<Integer, String> indexRequests) {
		this.type = type;
		this.requestingIndex = requestingIndex;
		this.indexRequests = indexRequests;
		this.indexResponses = new HashMap<Integer,String>();
	}

}
