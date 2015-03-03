%percentage definition
function RealPositivePoints = compareOtherTwoSamples(pointsA,arrayBA,arrayCA)
    RealPositivePointsBA = [];
    RealPositivePointsCA = [];
    for i = 1 : numel(arrayBA)
        if arrayBA(i) <= 3
            RealPositivePointsBA(end+1,:) = pointsA(i,:);
        end
    end
    
    for i = 1 : numel(arrayCA)
        if arrayCA(i) <= 3
            RealPositivePointsCA(end+1,:) = pointsA(i,:);
        end
    end 
    RealPositivePoints = unique(intersect(RealPositivePointsBA,RealPositivePointsCA,'rows'),'rows');
end