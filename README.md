# MobKillTrackerV2
Tracks totem stats for mythic farms

#### Note: This mod cannot detect if items were thrown by the player or dropped from the mob, hence if you have any cleaners, the numbers will go off.

Also please note that it can detect items that you have thrown. Extra detections for all loaded players will be in future releases.

## Usage
Everything's automated, timer starts when a totem placement is detected; stops when exactly 5 minutes has passed (due to totem expiration message will not always appear)

## Commands
Normal command is /mkt. Here are the available commands:
- start [time]: Starts logging for a specified amount of time, default 30s.
- stop: Terminates current logging and dump summary
- advanced: Shows detailed info (Item/ingredient rates)
- time: Shows/sets hotkey timer (Default M for triggering)

## API Calling
You may see the API calls in API.java; the mod calls for data exactly 5 times per session (and extra 5 every time you recollect API)

## Debug commands
Command is always /mkt-debug (or /mktd for short)

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

- IndexOOB error may occur during debug (Not sure why, seems to be an internal error)
- Inaccuracies may occur when throwing items in bulk
- Items thrown by other players are not detected 
