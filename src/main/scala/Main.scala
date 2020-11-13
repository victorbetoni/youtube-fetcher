import java.util._

import scala.collection.mutable.HashMap

import com.google.api.services.youtube._
import com.google.api.services.youtube.model.Video

import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestInitializer

object Main extends App {
  val props = new Properties()
  props.load(getClass.getClassLoader.getResourceAsStream("key.properties"))

  val apiKey = props.getProperty("apikey")
  val transport = new NetHttpTransport()
  val factory = new JacksonFactory()
  val httpResquestInitializar = new HttpRequestInitializer {
    override def initialize(request: HttpRequest) { }
  }

  val youtube = new YouTube.Builder(transport, factory, httpResquestInitializar)
    .setApplicationName("Youtube Fetcher")
    .setYouTubeRequestInitializer(new YouTubeRequestInitializer(apiKey))
    .build

  def getVideoData(video: Video): HashMap[String, Object] = {
    var map = new HashMap[String, Object]
    map.addOne("description" -> video.getSnippet.getDescription)
    map.addOne("channel" -> video.getSnippet.getChannelId)
    map.addOne("title" -> video.getSnippet.getTitle)
    map.addOne("likes" -> video.getStatistics.getLikeCount)
    map.addOne("dislikes" -> video.getStatistics.getDislikeCount)
    map.addOne("views" -> video.getStatistics.getViewCount)
    map.addOne("favorites" -> video.getStatistics.getFavoriteCount)
    map.addOne("comments" -> video.getStatistics.getCommentCount)
    map
  }

  def getVideo(id: String): Option[Video] = {
    Some(youtube.videos.list("snippet,statistics,localizations").setId(id).execute.getItems.get(0))
  }

  def query(query: String, resultLimit: Int): Set[Video] = {
    var results: Set[Video] = new HashSet()
    val search = youtube.search.list("id,snippet").setQ(query).setType("video").setMaxResults(resultLimit)
    search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)")
    search.execute.getItems.forEach(result => results.add(getVideo(result.getId().getVideoId()).get))
    results
  }
}