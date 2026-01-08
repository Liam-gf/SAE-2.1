# Application MPM ( Méthode des Potentiels et antécédents Métra )

## Compilation

Avant d'exécuter l'application, il faut compiler les fichiers `.java` vers le dossier `class`.
Pour cela :

### Sous Windows

Ouvrir l'invite de commandes (CMD) et se positionner dans le dossier nommé `source`. Ensuite faite :

`javac -d ..\class "@compile.list"`


### Sur Linux

Ouvrir l'invite de commandes (CMD) et se positionner dans le dossier nommé `source`. Ensuite faite :

`javac -d ../class @compile.list`


## Execution de l'application


On execute la commande :

`java -cp ../class mpm.Controleur`