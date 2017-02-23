package so.blacklight.swarm.http.json

import com.google.gson.Gson
import spark.ResponseTransformer

/**
	* Created by xea on 2/18/2017.
	*/
class JSONTransformer extends ResponseTransformer {

	private val gson: Gson = new Gson()

	override def render(model: scala.Any): String = gson.toJson(model)
}
