# Kitchen Runner

## What is it?
Kitchen runner is a helpful plugin to have during gamemodes with auto-agility training gear, like the Sage's greaves. The plugin will essentially allow you to customize when and if players in the lumbridge kitchen area will be hidden or not.

By default, the plugin will show you the correct tile to stand on when starting, only if you are not currently on them. It also opts to hide everyone except the player you specified as the conductor, until you are following them on the correct cycle (though these are customizable).

## Why not just entity hider and tile indicator?
Entity hider by default will only work for friends if the player has added you back and is "online". If they do not have you added back and are set to private, even if they are around you, they will not show when the option "hide friends" is unchecked. This is for privacy reasons, which I actually really appreciate. However, this impacts how easy it is to get a follow circle started if the person running it is set to private.

Tile indicators is nice to highlight your true tile, but from my understanding, most "target" true tile features of this plugin, and others like it, typically only work on NPCs and mobs. This also automatically pre-configures which player to highlight the tile of (yourself and the conductor) as well as dynamically shows and hides the correct tiles you need to be running on.

All put together, the features are specially crafted to fit together in such a way that this plugin can be minimally intrusive visually, right up until you actually _need_ to use its features.

## Features
- Dynamic entity hiding of everyone that isn't the conductor, which is configurable to be on _always, never, following, or not_
- Tile markers on the 6 correct tiles for you to run on
- Customizable colors for player tile color, conductor tile color, and starting tile color
- Configurable notifications for when you stop interacting with (i.e. stop following) the specified conductor
- Configurable notification for when the conductor types an "alert: some message" chat message (default disabled)

## Contribute
Here are the steps for contributing

### Bug fix
1. Create a fork of the project
2. Create a branch from the support branch, with a short descriptive name of what your change/fix is
3. Bump the version in build.gradle to the next patch version plus "-SNAPSHOT" (see [semver](https://semver.org/) explaination)
4. Once tested, create a PR from your forked branch to the support branch
5. If there is an associated issue with your PR, specify that in the PR as "Fixes/Closes/Implements issue #10" or whatever the issue number is
6. Once I review it and test it myself, I will merge it to master, remove the snapshot, tag it and make a PR to the plugin hub

### Feature
1. Create a fork of the branch
2. Create a branch from the dev branch, with a short descriptive name of what your change/fix is
3. Once tested, create a PR from your forked branch to the dev branch
4. If there is an associated issue with your PR, specify that in the PR as "Closes/Implements issue #10" or whatever the issue number is
5. Once I review it and test it myself, I will merge it to dev. It will be included when the next batch of changes gets deployed to the hub.

## Pictures
<img width="425" height="246" alt="illustration of a player standing on the wrong tiles, who is not following the conductor" src="https://github.com/user-attachments/assets/9dbaf984-53c5-4a2c-8806-967090ee0651" /><br/>
> A player who is not standing on the correct starting tiles, and is not following the conductor

<img width="409" height="255" alt="image" src="https://github.com/user-attachments/assets/4b351044-7e55-421a-be90-7ad90d38a592" /><br/>
> A player who _is_ standing on the right tiles, but is not following the conductor

<img width="471" height="297" alt="image" src="https://github.com/user-attachments/assets/cc2b2246-15d0-4ff3-8e60-16f983d221e5" /><br/>
> A player who is both standing on the right tiles and is following the conductor (aka "in cycle")
