%The Main Work Flows
%Notes:
%originalPoints(1:A,2:B,3:C)
%samples(1:AB,2:AC,3:BC,4:BA,5:CA,6:CB)
%
%draw Samples Figure (Distance Bar)
drawSamplesFigure;
%draw DistanceCircle
drawDistanceCircle(sample1,sample2,sample3,sample4,sample5,sample6);
%Calculate distace ROC parameters
SEarray=[];
FPRarray=[];
%A-BC
[SEarray(end+1),SP,FPRarray(end+1)] = positiveNegationDefV3(originalPoints2,originalPoints3,sample1,sample2,sample6,sample3);
%B-AC
[SEarray(end+1),SP,FPRarray(end+1)] = positiveNegationDefV3(originalPoints1,originalPoints3,sample4,sample3,sample5,sample2);
%C-AB
[SEarray(end+1),SP,FPRarray(end+1)] = positiveNegationDefV3(originalPoints1,originalPoints2,sample5,sample6,sample4,sample1);
%draw ROC potins graph
drawROCpoints(FPRarray,SEarray);
