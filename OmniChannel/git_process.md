# README #

This README would normally document whatever steps are necessary to get your application up and running.

### What is this repository for? ###

* Quick summary
* Version
* [Learn Markdown](https://bitbucket.org/tutorials/markdowndemo)

### How do I get set up? ###

* Summary of set up
* Configuration
* Dependencies
* Database configuration
* How to run tests
* Deployment instructions

### Contribution guidelines ###

* Writing tests
* Code review
* Other guidelines

### Who do I talk to? ###

* Repo owner or admin
* Other community or team contact

# Branching and Forking Strategy

The main repository is under ZyterAdmin's workspace and each user who has access to Bitbucket will have access to their
own Bitbucket workspaces. For example a user named "John" who is granted access to Bitbucket will have access to 
their own workspace named "John".

Omnichannel repository should not be cloned directly on developer machines, rather the developer should Fork the 
Omnichannel repository into their own workspace. When forking is done successfully we can see the Omnichannel code base
under the current users workspace. This forked repository should be cloned on the developers machine. When forking happens 
all branches under the central repository are also forked. So the developer can clone the fork repository and checkout
any existing branches. In this case the developer should checkout the Development branch. 

Once the Development branch is checked out it should be noted that the developer is on the Development branch
of your local workspace. Now we must set up the upstream repository for the purpose of pulling any commits made to the 
central repository. This is needed for keeping in sync the developer repository with the central repository. 
The upstream repository can be set by using the follwing commands.   

<br>

### command to view all the upstream repositories  

<br>

```
         git remote -v
		 
		 output:  
		 origin	git@bitbucket.org:john/omnichannel.git (fetch)
         origin	git@bitbucket.org:john/omnichannel.git (push)
```

In the above command we can see that remote repository pointing to the developer's forked repository.

Now we can add the URL to central repository as a upstream repository as follows:

### command to add upstream repository  

<br>

```
         git remote add upstream git@bitbucket.org:zyteradmin/omnichannel.git
```

### Now running the command to view all the upstream repositories  

<br>

```
         git remote -v
		 
		 output:  
		 origin	git@bitbucket.org:john/omnichannel.git (fetch)
         origin	git@bitbucket.org:john/omnichannel.git (push)
		 upstream	git@bitbucket.org:zyteradmin/omnichannel.git (fetch)
		 upstream	git@bitbucket.org:zyteradmin/omnichannel.git (push)
```
<br>


## Developer workflow

<br>

Once the repository is forked, the developer can see all the existing branches (Development, QA, UAT and others) 
which were created in the zyterAdmin/Omnichannel repository. So we can checkout the Development repository and under this branch the 
developer should create his/her own feature branch. The feature branch must be created with exact 
ticket ID (example: ZCEA-360). The developer completes his development activity on this branch and commits and pushes
the changes to this branch with proper comiit message. The changes from the central repository can be pulled to the feature branch using the 
below command.

### example for commit with appropriate message 
<br>

```
          git commit -m"ZCEA-360: Audio video call integration"
```

<br>

```
          git pull upstream Development
```

The command will bring the latest commits from central repository's Development branch on to your Feature branch.
Once the code is committed and pushed to feature branch and all the latest commits from the central repository
are pulled to feature branch, Pull Request is raised to the central repository.

This completes the Developer workflow.

The advantage of this strategy is that the central repository will be free from all the feature branches and the git tree
will be clean. The developers should take care to delete the feature branches from their local/forked repositories.

## Branches in central repository

We plan to maintain only four branches in Central repository

* Main
* Development
* QA
* UAT

All the feature based Pull requests will be merged to ** Development ** branch. The developers should deploy this 
branch to their Development environment and do the testing here.

Then the commits from the Development branch are pulled to ** QA **  branch. Some times we may have to cherry pick 
the specific commits if only certain features are required for QA release. From QA branch the code is deployed to QA
Environment.

The same process is applicabale for ** UAT ** branch and UAT release. Once the UAT is done the UAT code branch is merged 
to Main branch and released to Production.

Aditionally we can tag the Previous Production release code (current Main) for any rollback related activities.
