package com.travelplan.engagement;

import java.util.*; import org.neo4j.driver.*; import org.springframework.stereotype.Service;

@Service
class RecommendationService {
 private final Driver driver;
 RecommendationService(Driver driver){this.driver=driver;}
 void participated(UUID user,TravelClient.TravelView travel){write(user,travel,0);}
 void rated(UUID user,TravelClient.TravelView travel,int rating){write(user,travel,rating);}
 private void write(UUID user,TravelClient.TravelView t,int rating){
  try(Session session=driver.session()){session.executeWrite(tx->{tx.run("MERGE (u:Traveler {id:$uid}) MERGE (t:Travel {id:$tid}) SET t.destination=$destination,t.activities=$activities,t.accommodation=$accommodation,t.transportation=$transportation MERGE (u)-[p:PARTICIPATED]->(t) SET p.rating=CASE WHEN $rating=0 THEN coalesce(p.rating,0) ELSE $rating END",Values.parameters("uid",user.toString(),"tid",t.id().toString(),"destination",t.destination(),"activities",t.activities(),"accommodation",t.accommodation(),"transportation",t.transportation(),"rating",rating)).consume();return null;});}catch(Exception ignored){ }
 }
 List<UUID> recommend(UUID user){
  try(Session session=driver.session()){return session.executeRead(tx->tx.run("MATCH (u:Traveler {id:$uid})-[:PARTICIPATED]->(seen:Travel), (candidate:Travel) WHERE NOT (u)-[:PARTICIPATED]->(candidate) WITH candidate, sum(CASE WHEN candidate.destination=seen.destination THEN 1 ELSE 0 END + CASE WHEN candidate.activities=seen.activities THEN 1 ELSE 0 END + CASE WHEN candidate.accommodation=seen.accommodation THEN 1 ELSE 0 END) AS score WHERE score>0 RETURN candidate.id AS id ORDER BY score DESC LIMIT 10",Values.parameters("uid",user.toString())).list(r->UUID.fromString(r.get("id").asString())));}catch(Exception ignored){return List.of();}
 }
}
