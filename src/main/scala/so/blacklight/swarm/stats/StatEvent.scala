package so.blacklight.swarm.stats

class StatEvent

case class IncrementCounter(counter: String) extends StatEvent
case class DecrementCounter(counter: String) extends StatEvent
case class GetCounterValue(counter: String) extends StatEvent
case class CounterValue(counter: String, value: Option[Long]) extends StatEvent
case class BatchCounterValue(toMap: Map[String, Long]) extends StatEvent
