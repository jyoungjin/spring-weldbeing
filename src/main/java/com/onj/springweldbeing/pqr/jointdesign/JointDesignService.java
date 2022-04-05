package com.onj.springweldbeing.pqr.jointdesign;

import org.springframework.stereotype.Repository;

@Repository
public interface JointDesignService {
    int insertPQRJointDesign(JointDesign jointDesign);
}
