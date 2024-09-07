local p = peripheral.find("geoScanner")
test.assert(p, "There is no scanner")
local result, err = p.scan(3)
test.eq(nil, err, "Err should be nil")
test.assert(result, "There is no scan result")
sleep(2.5)
result, err = p.chunkAnalyze()
test.eq(nil, err, "Err should be nil")
test.eq(result, {}, "Chunk analyze result should be empty")