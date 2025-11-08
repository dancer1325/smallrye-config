package org.example;

import io.smallrye.config.SmallRyeConfig;
import io.smallrye.config.SmallRyeConfigBuilder;
import org.eclipse.microprofile.config.ConfigProvider;

public class App
{
    public static void main( String[] args )
    {
        // 1. retrieve SmallRyeConfig instance
        SmallRyeConfig retrieveSmallRyeConfig = ConfigProvider.getConfig().unwrap(SmallRyeConfig.class);
        System.out.println(retrieveSmallRyeConfig);

        // 2. build your OWN SmallRyeConfig instance
        SmallRyeConfig buildNewSmallRyeConfig = new SmallRyeConfigBuilder().build();
        System.out.println( buildNewSmallRyeConfig );
    }
}
