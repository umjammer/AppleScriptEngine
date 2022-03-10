tell application "Finder" to activate

set lasttime to current date
set min to 5 as number
set interval to 1 as number

repeat
    display notification "Notification" with title "Title"
    delay (interval * 60)
    if (((current date) - lasttime) / minutes) ≥ min then exit repeat
end repeat
