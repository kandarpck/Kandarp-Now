#Kandarp-Now

Building a Personal Assistant that works for you and gives you the information when you need. 

#Architecture

##High Level Framework
- TODO

##Backend Setup
- TODO

##GCM Framework
![alt tag](https://raw.github.com/kandarpck/Kandarp-Now/master/assets/architecture.png)

##Apache Nutch
- A well matured, production ready crawler. It enables fine grained configuration, relying on Apache Hadoop data structures, which are great for batch processing.
- TODO

##Apache Solr
- TODO

##Google Compute Engine
- TODO

##Additional Materials
- [Presentation files](Kandarp-Now/master/assets/BE%20Project%20Presentation%20Feb-March%202015.pdf)


##Features (Till now)
- Cloud Connected for personal registration and data storage
- Google Cloud Messaging *(7 distinct cases to cater to all the scenarios of app use)*
- Weather Cards *(OpenWeather API)*
- Traffic and Location Data *(Time to home and work using Maps API)*
- Movies, Trailers and recent trending songs *(YouTube API)*
- Finance, Stocks *(Yahoo Finance API)*
- **Places:** Hospitals, Restaurants, ATMs, Airport, etc *(Places API and Maps API)*
- Connected to **my Search Engine** with an option to search on other as well
- Voice typing and Search done


##Best Practices followed
- Heavy use of GCM. Tickle messages from GCM to wake up device and manage sync
- Use of SyncAdapter
- Takes full advantage of Lollipop features including colors, cards, elevation, drop shadows, etc

##Connection to my own search engine
- **Entire configuration set up on Google Compute Engine**
- Apache Nutch used to crawl **~70000** web pages and created segments to be used by Apache Solr
- Solr server set up and running.
- All data parsed, stored, cleansed, optimized and cached by Solr Configuration
- Used inverted index for faster query processing
- *Server currently shut down to save costs. Search functionality in the app may not work :(*

##Screenshots
-

##Installation Steps
- After project completion


##TODO
- Proper Introduction
- Architecture Diagrams
- Use Case, Class, State Transition Diagrams
- Screenshots
- Final Report
- Setup Details and How to use
- Update Features, Best Practices
