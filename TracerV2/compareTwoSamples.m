%percentage definition
function RealPositivePoints = compareOtherTwoSamples(pointsA,pointsB,arrayAB,arrayBA)
    RealPositivePoints = [];
    for i = 1 : numel(arrayAB)
        if arrayAB(i) <= 3
            RealPositivePoints(end+1,:) = pointsB(i,:);
        end
    end
    
    for i = 1 : numel(arrayBA)
        if arrayAB(i) <= 3
            RealPositivePoints(end+1,:) = pointsA(i,:);
        end
    end 
end