Artistopedia:

This Api combines information from various sources and in particular (MusicBrainz, Cover Art Archive and Dicogz) to provide basic information 
about any Artist. This API is built on Java and Spring WebFlux.
Features:

    1.This Rest API is built on top of Spring Webflux and is designed to be reactive and non-blocking.
    2. The responses are cached in memory to improve performance for repeated requests . 
    3. All API calls are logged before sending the request and after receiving the response . This is done via using a “filter” on the WebClient (Webfluxes’ asycn http client”.
    4. Has a unified way of handling application level exceptions. (pls refer to : GloabalExceptionHandler.java.
    5. Takes advantage of various Spring’s utilities and features .
    6. Retry : All Api calls will be retried once , in case of failure 
    7. Partial response - Refer to Partial Response policy .
    8. I have chosen Discogs as the source for Artist profiles - this is primarily due to the fact that the API documents for it was provided .


Partial Response policy :
    I’m of the opinion that in dealing with partial responses, the best approach is to either return a JSON error response and indicate that something went wrong reject the request(Short circuit) or return the partial response but notify the user that what the user sees is in fact is a partial response .

    In case of the connection error to MusicBrainz API I decided to just return an error response and reject the request (Short circuit ). This is because both the Cover API as well as the Discogz APIs are dependent on this call. Should this Api call fail , it will be retried once , if the problem persists an error response shall be returned.

    In case of an intermittent error with any of the cover images (An Artist could have multiple albums hence multiple covers) - I have decided to retry once and if the error is persistent ,  I notify the user with a message that the specific cover image that the user has requested is missing or could not be found . While testing , I noticed some of the image URIs were returning a 404. I.E, if we have 30 albums and we only find an image for 26 of them , the four missing albums will have a message indicating that no image could be found for them.

    In case of an error on the Discogz API , I will return the Albums along with their associated images but indicate that the profile description could not be found.

    In general , except for the first call , the aggregate reponse is based on the best effor



 Responses will be idempotent . In case of an exception - retry will follow and eventually a JSON Rest error response will be returned to user .

Performance :

    The problem had not clearly stated whether this service needs to be able to handle periodic extreme high levels of loads in a highly available cloud environment where the service could quickly become replicated (horizontal scaling ) and automatically scaled and load balanced . I operated under the assumption that maximum concurrency should be achieved in one machine (vertical scaling) . I want to emphasis to ensure high availability and scalability it is best to consider containerizing this service and deploying it to the cloud and ensure automatic scalability via cloud solutions such as Kubernetes or AWS ECS Automatic scaling groups (ASG). 

Technology choice:
    The problem as it is stated is a set of highly intensive IO operations . As such, this problem is a prime candidate for a truly asynchronous web stack that is capable of handling high concurrency with small number of threads . Node.JS, Vert.x  were among the obvious choices . However, with the recent emergence of Spring Webflux , a reactive and non-blocking framework developed by Spring , it seemed to me that Webflux would be the more suitable choice . This decision was partly rooted in the fact that I was not quite ready to let go of rich features of Spring and given the time constraints and my more intimate knowledge of Spring as compared to the other similar technologies, I deemed Webflux to be the more prudent choice . The relatively recent Spring Webflux is essentially the Asynchronous and reactive version of the Spring boot , As it is based on non- blocking I/Os it is capable of handling a very high number of concurrent requests . For a performance comparison of  Spring Webflix vs Spring boot please refer to:
    https://medium.com/@the.raj.saxena/springboot-2-performance-servlet-stack-vs-webflux-reactive-stack-528ad5e9dadc

Disclaimer :Although I was familiar with Spring boot , the Spring webFlux was very much brand new to me. 


Caching considerations:

Requirement 
: Need a caching solution to enhance performance by leveraging the actively used data in memory
 All the reads and writes to cache need to occur asynchronously to fit with the reactive paradigm.

Technology choice :
Initially I gravitated towards using Ehcache (The configs for which are still commented (intentionally to showcase this unsuccessful integration effort ) which is an open source and reliable caching solution for Java due to its ease of usability; however, I quickly ran into serialization issues .Since the service returns  a Mono which really is a future( a promise in Js world ) and Mono does not implement serializable , we can not achieve disk storage (Overflow to disk) .  Additionally, Caching a Mono instance itself doesn't make sense as it is basically akin to caching an instance of callable\runnable . Perhaps another approach would have been to perform caching at  the JSON  level as opposed to object level . Unfortunately , as of right now , no out of the box spring based caching solution seems to have been integrated with Spring Webflux just yet. In light of this lack of built in Spring caching support , I opted to go with Caffeine cache as an in memory caching solution. Caffeine provides an asynchronous population strategy which lends itself nicely to our reactive paradigm at hand.  
For additional information on Caffeine , please refer to :

Note:
Although I did not explicitly specify an eviction strategy , based on the Caffein’s official documentation, it seems that Caffein Automatically configures itself based on a combination of LRU(Least recently used ),size based eviction and time based expiration (The former values are configurable and present in applicationConfig.java ) .


https://github.com/ben-manes/caffeine

https://github.com/ben-manes/caffeine/wiki/Population


Instructions to Run the application:
Via Maven:
1.Simply clone the project from github
2.Navigate to the directory 
3. mvn spring-boot:run


Via Docker:

    The application has already been dockerized .
    Navigate to the directory where the project exists .

Assuming you have docker installed 

    1.docker build -f Dockerfile -t docker-webflux .
    2. docker run -p 8085:8080  docker-webflux
    Or any other desired port ! Spring boot by default runs in 8080

Alternativly the application could be run via any IDE like any regular Spring Boot application:
    The entry point is : ArtistopediaApplication.java


Road way to high availability and scalability :

Again , true high availability could not be achieved with just one machine ,network outages may occur at any time or the node could go down and be inaccessible for various reasons . To achieve high availability we shall need to have a cluster of multiple instances of a service running . These containers need to have a way of finding each other  (Service discovery), they need to be accessed by and load needs to be distributed among them via a load balancer . The orchestration among these containers could be achieved via various docker orchestration solutions such as Kubernetes . 


Test MBIDS:

5b11f4ce-a62d-471e-81fc-a69a8278c7da
f27ec8db-af05-4f36-916e-3d57f91ecf5e
