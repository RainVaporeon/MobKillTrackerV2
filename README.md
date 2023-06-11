# MobKillTrackerV2
Tracks totem stats for mythic farms

# Deprecated Version

Please go to [this repository](https://github.com/RainVaporeon/MobKillTrackerv3) for the latest version. It has fixed various bugs and is generally more optimized.

## Features

- Automatically collects data in totem (Kills, item drops, etc)
- Commands that allow you to trace to any previous saved data (Within session)
- Hotkey (Default M) to allow you to start recording kills/drops in a specified time range.
- A "cleaner" option in case you have other players cleaning for you.

## Usage
Everything's automated, timer starts when a totem placement is detected; stops when exactly 5 minutes has passed (due to totem expiration message will not always appear)

## Commands
Normal command is /mkt. Here are the available commands (More in the mod!):
- start [time]: Starts logging for a specified amount of time, default 30s.
- stop: Terminates current logging and dump summary
- advanced: Shows detailed info (Item/ingredient rates)
- time: Shows/sets hotkey timer (Default M for triggering)

## API Calling
You may see the API calls in API.java; the mod calls for data exactly 5 times per session (and extra 5 every time you recollect API)

## Debug commands
Command is always /mkt-debug (or /mktd for short) (Only use if you know what you are doing!!! Log spammy!!!)

Commands are as follows:

- test: Starts a 30-second test, especially useful for testing.
- log: Starts a generic log including item and mob kill detections.
- adv: Starts an advanced log including all scanned entities and their data.
- end: Ends current scan and dumps result.
- recollect: Recollects data from the API.
- toggle: Toggles mod

## Builds

aaaa IntellIJ IDEA with Java 8u211

## Known Issues

- Inaccuracies may occur when throwing items in bulk
- General inaccuracies with item drops, mob kills far as we know, is accurate.
