# projectday-schedule
Small app to help organise people into groups according to their preferences

## Terms explanations
### Group
A group is a collection of people, used to help organise a lot of people, it doesn't really affect how the timetables are generated
### Stand
A stand is a place the people can visit, a seminar, for example. People assign priorities to stands, which can be imported in the Requests tab. It can have a maximum number of people assigned per block, this is a soft limit for manual assignments and a soft limit for the (NYI) generator.
### Request
A request is a person's perference, "how much does he want to go here", it is not a strict rule.
### Assignment
An assignment is where a person will actually go. It can be specified manually or (NYI) the program will generate the assignments according to the specified Requests.
### Block
The number of blocks specifies how many stands any person can attend. The number of blocks is strict and two blocks cannot overlap at this time. We suppose all stands have a programme of the same length and that all start and end at the same time.

## Checklist
- [x] Basic functionality
- [ ] Automatic assignments generation
- [ ] Blocks per stand
