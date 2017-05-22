package biz.jovido.libmori;

import biz.jovido.seed.EnableSeed;
import biz.jovido.seed.content.Structure;
import biz.jovido.seed.content.StructureService;
import biz.jovido.seed.content.field.AssetField;
import biz.jovido.seed.content.field.FragmentField;
import biz.jovido.seed.content.field.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

/**
 * @author Stephan Grundner
 */
@EnableSeed
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ConfigurableApplicationContext context =
                SpringApplication.run(Application.class, args);
        Assert.isTrue(context.isRunning());
    }

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    void init() {
        PlatformTransactionManager transactionManager = applicationContext
                .getBean(PlatformTransactionManager.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.execute((TransactionStatus status) -> {

            try {
                StructureService structureService = applicationContext.getBean(StructureService.class);
                Structure basicPage = new Structure("basicPage");
                basicPage.setLabel("title");

                TextField titleField = new TextField("title");
                titleField.setCapacity(1);
                basicPage.putField(titleField);

                TextField textField = new TextField("text");
                textField.setRows(6);
                basicPage.putField(textField);

                FragmentField otherField = new FragmentField("other");
                basicPage.putField(otherField);

                AssetField imageField = new AssetField("image");
                basicPage.putField(imageField);

                structureService.registerStructure(basicPage);

            } catch (Exception e) {
                status.setRollbackOnly();
                throw new RuntimeException(e);
            }

            return null;
        });
    }
}
