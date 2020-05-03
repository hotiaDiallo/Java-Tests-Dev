# MyERP

## Organisation du répertoire

*   `doc` : documentation
*   `docker` : répertoire relatifs aux conteneurs _docker_ utiles pour le projet
    *   `dev` : environnement de développement
*   `src` : code source de l'application


## Environnement de développement

Les composants nécessaires lors du développement sont disponibles via des conteneurs _docker_.
L'environnement de développement est assemblé grâce à _docker-compose_
(cf docker/dev/docker-compose.yml).

Il comporte :

*   une base de données _PostgreSQL_ contenant un jeu de données de démo (`postgresql://127.0.0.1:9032/db_myerp`)



### Lancement

    cd docker/dev
    docker-compose up


### Arrêt

    cd docker/dev
    docker-compose stop


### Remise à zero

    cd docker/dev
    docker-compose stop
    docker-compose rm -v
    docker-compose up
    
### Correctifs
* Dans l'entité `EcritureComptable`, correction de la méthode `getTotalCredit()` qui accédait à la méthode `getDebit()` au lieu de `getCredit()`
* Dans l'entité `EcritureComptable`, correction de la regex pour adapter la référence de sorte à avoir une réfence qui respecte le format suivant`AA-2016/00001`
* Dans l'entité `EcritureComptable`, correction de la méthode `isEquilibree()` qui retournait le résultat d'une égalité à l'aide de `equals()` au lieu de faire une comparaison avec `compareTo()`
* Dans la classe `ComptabiliteManagerImpl`, correction de la méthode `updateEcritureComptable()`. Ajouter la ligne `this.checkEcritureComptable(pEcritureComptable);` en haut afin de vérifier que la référence de l'écriture comptable respecte les règles de comptabilité 5 et 6
* Dans la classe `SpringRegistry` de la couche `business`, modification de la variable `CONTEXT_APPLI_LOCATION` afin d'adapter le chemin d'accès au fichier `bootstrapContext.xml` notre conteneur Spring IoC, on importe dans ce dernier le `businessContext.xml`, `consumerContext.xml` et le `datasourceContext.xml` qui va redéfinir le bean dataSourceMYERP pour les tests

### Ajouts

* Passer de JUnit 4 à JUnit 5
* Dans la classe `SequenceEcritureComptable` : ajout de l'attribut journalCode avec son getter et son setter

* Dans l'interface `ComptabiliteDao`, ajout d'une méthode `getSequenceByCodeAndAnneeCourante()`, son implémentation est fat dans `ComptabiliteDaoImpl` et la requête correspondante `SQLgetSequenceByCodeAndAnneeCourante` dans le fichier `sqlContext.xml`

* Dans l'interface ComptabiliteDao, ajout de la méthode `insertOrUpdateSequenceEcritureComptable`, implémentation de celle-ci dans `ComptabiliteDaoImpl` et définition de la requête correspondante `SQLupsertSequenceEcritureComptable` dans le fichier `sqlContext.xml`

* Dans l'interface `ComptabiliteManager`, ajout de la méthode `insertOrUpdateSequenceEcritureComptable()`, implémentation de celle-ci dans `ComptabiliteManagerImpl`

* Dans la classe `ComptabiliteManagerImpl`, implémentation de la méthode `addReference()`

* Dans la classe `ComptabiliteManagerImpl`,vérifications permettant le respect de la règle de comptabilité 5, à la fin de la méthode `checkEcritureComptableUnit()`

* Configuration des tests d'intégration de la couche consumer dans le dossier `test-consumer`

### Jenkins

* il ya un document de configuration de Jenkins dans doc/config-jenkins.pdf

### Déploiement

* `cd /src`
* `mvn clean install -P test-consumer,test-business`


