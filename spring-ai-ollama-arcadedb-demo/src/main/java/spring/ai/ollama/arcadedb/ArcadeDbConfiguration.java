package spring.ai.ollama.arcadedb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.arcadedb.database.Database;
import com.arcadedb.database.DatabaseFactory;


@Configuration
public class ArcadeDbConfiguration {
	private final Logger log = LoggerFactory.getLogger(ArcadeDbConfiguration.class);
	
	public ArcadeDbConfiguration() {
	}
	
	@Bean
	Database arcadeDbServer() {
		this.log.debug("arcadeDbServer");
		
		try(DatabaseFactory arcade = new DatabaseFactory("C:/Temp/arcadedb")) {
			if(arcade.exists() == false) {
				Database db = arcade.create();
				
				this.log.debug("New ArcadeDb: {}", db);
				return db;
			}
			
			Database db = arcade.open();
			
			this.log.debug("Existed ArcadeDb: {}", db);
			
			return db;
		}

	}

}
